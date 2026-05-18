package com.example.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {

    @Test
    void shouldReturnUserNameForValidId() {
        UserService service = new UserService(); // userRepository is null — bug!

        // Throws NullPointerException: userRepository was never injected
        String name = service.getUserName(42L);

        assertEquals("Alice", name);
    }

    @Test
    void shouldThrowForNegativeId() {
        UserService service = new UserService();

        // Also hits the same NPE before any ID validation can happen
        service.getUserName(-1L);
    }
}
