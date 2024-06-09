package com.caiosantana.ecommerce.payment;

import com.caiosantana.ecommerce.customer.CustomerResponse;
import com.caiosantana.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
