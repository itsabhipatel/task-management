package com.example.taskmanagement.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

class SecurityHandlersTest {

    @Test
    void authenticationEntryPointReturnsUnauthorizedMessage() throws Exception {
        CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        entryPoint.commence(request, response, new BadCredentialsException("bad"));

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        assertEquals("{\"message\":\"Authentication failed. Please login with valid credentials.\"}", writer.toString());
    }

    @Test
    void accessDeniedHandlerReturnsForbiddenMessage() throws Exception {
        CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);
        StringWriter writer = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        handler.handle(request, response, new AccessDeniedException("denied"));

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        assertEquals("{\"message\":\"Authorization failed. You do not have permission to access this API.\"}", writer.toString());
    }
}
