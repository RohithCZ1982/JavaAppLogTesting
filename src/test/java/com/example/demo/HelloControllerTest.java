package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void homeReturnsMessage() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello from JavaAppLogTesting!"));
    }

    @Test
    void greetWithDefaultName() throws Exception {
        mockMvc.perform(get("/greet"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello, World!"));
    }

    @Test
    void greetWithCustomName() throws Exception {
        mockMvc.perform(get("/greet").param("name", "Rohith"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Hello, Rohith!"));
    }
}
