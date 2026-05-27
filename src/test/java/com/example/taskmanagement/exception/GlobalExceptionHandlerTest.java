package com.example.taskmanagement.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.ApiErrorResponse;
import com.example.taskmanagement.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

    @Test
    void shouldReturnBadRequestErrorResponse() {
        when(request.getRequestURI()).thenReturn("/api/tasks/create");

        var response = exceptionHandler.handleBadRequest(
                new IllegalArgumentException("Invalid request"),
                request);

        ApiErrorResponse body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Invalid request", body.getMessage());
    }

    @Test
    void shouldReturnValidationErrorResponse() {
        when(request.getRequestURI()).thenReturn("/api/tasks/create");
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "taskRequestDto");
        bindingResult.addError(new FieldError("taskRequestDto", "title", "", false, null, null, "Title is required"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(
                Mockito.mock(MethodParameter.class),
                bindingResult);

        var response = exceptionHandler.handleValidationException(exception, request);

        ApiErrorResponse body = response.getBody();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(body);
        assertEquals(400, body.getStatus());
        assertEquals("Validation Failed", body.getError());
        assertEquals("Request validation failed", body.getMessage());
        assertEquals("/api/tasks/create", body.getPath());
        assertEquals(1, body.getValidationErrors().size());
        assertEquals("title", body.getValidationErrors().get(0).getField());
        assertEquals("Title is required", body.getValidationErrors().get(0).getMessage());
        assertEquals("", body.getValidationErrors().get(0).getRejectedValue());
        assertNotNull(body.getTimestamp());
    }

    @Test
    void shouldReturnMultipleRuntimeValidationErrors() {
        var response = exceptionHandler.handleBadRequestRuntimeException(
                new BadRequestRuntimeException(List.of("First", "Second")));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertNotNull(body);
        assertEquals(List.of("First", "Second"), body.get("errors"));
    }

    @Test
    void shouldReturnSingleRuntimeValidationError() {
        var response = exceptionHandler.handleBadRequestRuntimeException(
                new BadRequestRuntimeException("Only one"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertNotNull(body);
        assertEquals("Only one", body.getMessage());
    }
}
