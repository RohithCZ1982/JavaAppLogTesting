package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // Bug: dependency is never injected — will be null at runtime
    private UserRepository userRepository;

    public String getUserName(Long id) {
        logger.info("Fetching user with id={}", id);
        // NullPointerException here because userRepository was never set
        return userRepository.findNameById(id);
    }
}
