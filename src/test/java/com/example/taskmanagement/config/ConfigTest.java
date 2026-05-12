package com.example.taskmanagement.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

class ConfigTest {

    @Test
    void openApiContainsApiInfo() {
        OpenAPI openAPI = new OpenApiConfig().taskManagementOpenApi();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Task Management API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }
}
