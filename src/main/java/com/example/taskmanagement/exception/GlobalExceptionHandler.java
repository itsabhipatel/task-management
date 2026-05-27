package com.example.taskmanagement.exception;

import com.example.taskmanagement.dto.ApiErrorResponse;
import com.example.taskmanagement.dto.ErrorResponse;
import com.example.taskmanagement.dto.ValidationError;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception,
                                                                   HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException exception,
                                                                 HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password.", request);
    }

    
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception exception,
                                                            HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        List<ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    ValidationError validationError = new ValidationError();
                    validationError.setField(error.getField());
                    validationError.setMessage(error.getDefaultMessage());
                    validationError.setRejectedValue(error.getRejectedValue());
                    return validationError;
                })
                .toList();

        ApiErrorResponse response = new ApiErrorResponse();
        response.setTimestamp(LocalDateTime.now());
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError("Validation Failed");
        response.setMessage("Request validation failed");
        response.setPath(request.getRequestURI());
        response.setValidationErrors(errors);

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDatabaseError(DataAccessException exception,
                                                               HttpServletRequest request) {
        exception.printStackTrace();
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database operation failed.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception,
                                                                  HttpServletRequest request) {
        exception.printStackTrace();
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected server error.", request);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status,
                                                               String message,
                                                               HttpServletRequest request) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(BadRequestRuntimeException.class)
    public ResponseEntity<?>  handleBadRequestRuntimeException(BadRequestRuntimeException ex) {

        // Multiple errors
        if (ex.getErrors()
                != null
                && !ex.getErrors()
                .isEmpty()) {

            Map<String, Object>
                    response =
                    new HashMap<>();

            response.put(
                    "errors",
                    ex.getErrors());

            return ResponseEntity
                    .status(
                            HttpStatus.BAD_REQUEST)
                    .body(response);
        }

        // Single error
        ErrorResponse
                errorResponse =
                new ErrorResponse(
                        ex.getMessage());

        return ResponseEntity
                .status(
                        HttpStatus.BAD_REQUEST)
                .body(
                        errorResponse);
    }

}
