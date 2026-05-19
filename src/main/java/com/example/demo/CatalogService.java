package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates ConcurrentModificationException and ClassCastException.
 */
@Service
public class CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogService.class);

    private final List<String> activeItems = new ArrayList<>(
        List.of("item-A", "item-B", "EXPIRED-C", "item-D", "EXPIRED-E")
    );

    // Bug: removes from the list while iterating with a for-each loop
    // ConcurrentModificationException is thrown on the second removal attempt
    public void removeExpiredItems() {
        logger.info("Scanning {} items for expiry", activeItems.size());
        for (String item : activeItems) {
            if (item.startsWith("EXPIRED")) {
                logger.warn("Removing expired item='{}'", item);
                activeItems.remove(item); // ConcurrentModificationException
            }
        }
        logger.info("Remaining items: {}", activeItems);
    }

    // Bug: stores mixed types in a raw List, then blindly casts every element to Integer
    @SuppressWarnings({"unchecked", "rawtypes"})
    public int sumPrices() {
        logger.info("Summing catalog prices");
        List rawPrices = new ArrayList();
        rawPrices.add(100);
        rawPrices.add(200);
        rawPrices.add("FREE");   // String slipped in via raw type
        rawPrices.add(50);

        int total = 0;
        for (Object entry : rawPrices) {
            // ClassCastException when entry is "FREE"
            total += (Integer) entry;
        }
        logger.info("Total price={}", total);
        return total;
    }
}
