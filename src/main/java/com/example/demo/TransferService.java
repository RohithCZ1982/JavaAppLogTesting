package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Demonstrates thread deadlock and IllegalStateException.
 */
@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final Object lockA = new Object();
    private final Object lockB = new Object();

    private boolean closed = false;

    // Bug: two threads acquire lockA→lockB and lockB→lockA respectively —
    // classic deadlock; both threads wait forever.
    public void transferAtoB(double amount) {
        logger.info("Thread '{}' acquiring lockA for A→B transfer of {}", Thread.currentThread().getName(), amount);
        synchronized (lockA) {
            logger.info("Thread '{}' holds lockA, waiting for lockB", Thread.currentThread().getName());
            synchronized (lockB) {           // deadlock if another thread holds lockB and waits for lockA
                logger.info("Transfer A→B amount={} complete", amount);
            }
        }
    }

    public void transferBtoA(double amount) {
        logger.info("Thread '{}' acquiring lockB for B→A transfer of {}", Thread.currentThread().getName(), amount);
        synchronized (lockB) {
            logger.info("Thread '{}' holds lockB, waiting for lockA", Thread.currentThread().getName());
            synchronized (lockA) {           // deadlock — lock acquisition order is reversed
                logger.info("Transfer B→A amount={} complete", amount);
            }
        }
    }

    // Bug: service can be closed mid-operation; no guard before use
    public void close() {
        logger.info("Closing TransferService");
        closed = true;
    }

    public void validateAndTransfer(String fromAccount, String toAccount, double amount) {
        // IllegalStateException: called after close()
        if (closed) {
            logger.error("TransferService is already closed — cannot process transfer from={} to={}", fromAccount, toAccount);
            throw new IllegalStateException("TransferService has been closed and cannot accept new transfers");
        }
        logger.info("Validated transfer from={} to={} amount={}", fromAccount, toAccount, amount);
    }
}
