package com.caiosantana.ecommerce.order;

import com.caiosantana.ecommerce.customer.CustomerClient;
import com.caiosantana.ecommerce.exception.BusinessException;
import com.caiosantana.ecommerce.kafka.OrderConfirmation;
import com.caiosantana.ecommerce.kafka.OrderProducer;
import com.caiosantana.ecommerce.orderline.OrderLineRequest;
import com.caiosantana.ecommerce.orderline.OrderLineService;
import com.caiosantana.ecommerce.payment.PaymentClient;
import com.caiosantana.ecommerce.payment.PaymentRequest;
import com.caiosantana.ecommerce.product.ProductClient;
import com.caiosantana.ecommerce.product.PurchaseRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private  final PaymentClient paymentClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;

    public Integer createOrder(OrderRequest request) {

        // check the customer --> customer-ms (using OpenFeign)
        var customer = this.customerClient
                .findCustomerById(
                        request.customerId()
                )
                .orElseThrow(() ->
                        new BusinessException(
                                "Cannot create order:: No Customer exists with the provided ID:: "
                                        + request.customerId()
                        )
                );

        // purchase the products --> product-ms (using RestTemplate)
        var purchasedProducts = this.productClient.purchaseProducts(request.products());

        // persist order
        var order = this.repository.save(mapper.toOrder(request));

        // persist order lines
        for (PurchaseRequest purchaseRequest : request.products()) {
            orderLineService.saveOrderLine(
                    new OrderLineRequest(
                            null,
                            order.getId(),
                            purchaseRequest.productId(),
                            purchaseRequest.quantity()
                    )
            );
        }

        //start payment process
        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );

        paymentClient.requestOrderPayment(paymentRequest);

        // send the order confirmation --> notification-ms (kafka)
        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchasedProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository
                .findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "No order found with the provided ID: %d",
                        orderId
                )));
    }
}