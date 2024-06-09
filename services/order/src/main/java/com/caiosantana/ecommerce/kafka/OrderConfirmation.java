package com.caiosantana.ecommerce.kafka;

import com.caiosantana.ecommerce.customer.CustomerResponse;
import com.caiosantana.ecommerce.order.PaymentMethod;
import com.caiosantana.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}