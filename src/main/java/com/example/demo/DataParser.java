package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Demonstrates NumberFormatException and ArrayIndexOutOfBoundsException.
 */
@Component
public class DataParser {

    private static final Logger logger = LoggerFactory.getLogger(DataParser.class);

    // Bug: caller passes raw user input directly — no validation before parseInt
    public int parseQuantity(String raw) {
        logger.info("Parsing quantity from input='{}'", raw);
        // NumberFormatException when raw is e.g. "abc" or ""
        int qty = Integer.parseInt(raw);
        logger.info("Parsed quantity={}", qty);
        return qty;
    }

    // Bug: assumes CSV always has at least 3 columns — no length check
    public String extractThirdColumn(String csvLine) {
        logger.info("Extracting third column from line='{}'", csvLine);
        String[] parts = csvLine.split(",");
        // ArrayIndexOutOfBoundsException when the line has fewer than 3 fields
        String value = parts[2];
        logger.info("Extracted value='{}'", value);
        return value;
    }
}
