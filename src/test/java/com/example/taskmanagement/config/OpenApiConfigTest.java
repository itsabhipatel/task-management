package com.example.taskmanagement.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    @Test
    void taskManagementOpenApiReturnsMetadata() {
        OpenAPI openAPI = new OpenApiConfig().taskManagementOpenApi();

        assertEquals("Task Management API", openAPI.getInfo().getTitle());
        assertEquals("1.0", openAPI.getInfo().getVersion());
        assertEquals("Simple Task CRUD API with Employee and Category", openAPI.getInfo().getDescription());
    }
}
