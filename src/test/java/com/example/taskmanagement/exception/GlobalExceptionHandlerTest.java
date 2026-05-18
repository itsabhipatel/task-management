package com.example.taskmanagement.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void shouldReturnNotFoundErrorResponse() {
        when(request.getRequestURI()).thenReturn("/api/tasks/by-id/99");

        var response = exceptionHandler.handleResourceNotFound(
                new ResourceNotFoundException("Task not found with id: 99"),
                request);

        ApiErrorResponse body = response.getBody();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(body);
        assertEquals(404, body.getStatus());
        assertEquals("Task not found with id: 99", body.getMessage());
        assertEquals("/api/tasks/by-id/99", body.getPath());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldReturnUnauthorizedErrorResponse() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        var response = exceptionHandler.handleBadCredentials(
                new BadCredentialsException("Invalid credentials"),
                request);

        ApiErrorResponse body = response.getBody();
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(body);
        assertEquals(401, body.getStatus());
        assertEquals("Invalid username or password.", body.getMessage());
    }
}
