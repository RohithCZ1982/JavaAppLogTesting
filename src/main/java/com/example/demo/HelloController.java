package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HelloController {

    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @GetMapping("/")
    public Map<String, String> home() {
        logger.info("GET / called");
        return Map.of(
            "message", "Hello from JavaAppLogTesting!",
            "timestamp", LocalDateTime.now().toString()
        );
    }

    @GetMapping("/greet")
    public Map<String, String> greet(@RequestParam(defaultValue = "World") String name) {
        logger.info("GET /greet called with name={}", name);
        return Map.of(
            "message", "Hello, " + name + "!",
            "timestamp", LocalDateTime.now().toString()
        );
    }
}
