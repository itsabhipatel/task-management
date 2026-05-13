package com.example.taskmanagement.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    void homeRedirectsToSwaggerUi() {
        assertThat(new HomeController().home()).isEqualTo("redirect:/swagger-ui/index.html");
    }
}
