package com.example.taskmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @InjectMocks
    private HomeController homeController;

    @Test
    void shouldRedirectToSwaggerUi() {
        String result = homeController.home();

        assertEquals("redirect:/swagger-ui/index.html", result);
    }
}
