package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);

    public double calculateTotal(int quantity, double unitPrice) {
        logger.info("Calculating total for quantity={} price={}", quantity, unitPrice);

        // Bug: calculateDiscount does not exist — compile error
        double discount = calculateDiscount(quantity, unitPrice);
        return (quantity * unitPrice) - discount;
    }
}
