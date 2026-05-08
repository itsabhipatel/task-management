package com.example.taskmanagement.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

class SecuritySupportTest {

    @Test
    void authenticationEntryPointWritesJsonResponse() throws Exception {
        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        new CustomAuthenticationEntryPoint().commence(null, response, new BadCredentialsException("bad"));

        org.mockito.Mockito.verify(response).setStatus(401);
        org.mockito.Mockito.verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        assertThat(body.toString()).contains("Authentication failed");
    }

    @Test
    void accessDeniedHandlerWritesJsonResponse() throws Exception {
        jakarta.servlet.http.HttpServletResponse response = mock(jakarta.servlet.http.HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        new CustomAccessDeniedHandler().handle(null, response, new AccessDeniedException("denied"));

        org.mockito.Mockito.verify(response).setStatus(403);
        org.mockito.Mockito.verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
        assertThat(body.toString()).contains("Authorization failed");
    }

    @Test
    void securityConfigExposesPasswordEncoderAndAuthenticationManager() throws Exception {
        JwtAuthenticationFilter filter = mock(JwtAuthenticationFilter.class);
        CustomAuthenticationEntryPoint entryPoint = mock(CustomAuthenticationEntryPoint.class);
        CustomAccessDeniedHandler deniedHandler = mock(CustomAccessDeniedHandler.class);
        SecurityConfig config = new SecurityConfig(filter, entryPoint, deniedHandler);
        AuthenticationConfiguration authenticationConfiguration = mock(AuthenticationConfiguration.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);

        PasswordEncoder encoder = config.passwordEncoder();

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
        assertThat(config.authenticationManager(authenticationConfiguration)).isSameAs(authenticationManager);
    }
}
