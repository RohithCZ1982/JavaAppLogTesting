package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Demonstrates StackOverflowError via unbounded recursion.
 */
@Component
public class RecursiveProcessor {

    private static final Logger logger = LoggerFactory.getLogger(RecursiveProcessor.class);

    // Bug: base case checks n == 0 but input can be negative,
    // making the recursion infinite for any n < 0.
    public long factorial(long n) {
        logger.debug("factorial({})", n);
        if (n == 0) {
            return 1;
        }
        // StackOverflowError for n < 0 — recursion never terminates
        return n * factorial(n - 1);
    }

    // Bug: helper and main method call each other with no termination condition
    public int countDown(int n) {
        logger.debug("countDown({})", n);
        // StackOverflowError — mutual recursion with no base case
        return countDownHelper(n);
    }

    private int countDownHelper(int n) {
        return countDown(n - 1);
    }
}
