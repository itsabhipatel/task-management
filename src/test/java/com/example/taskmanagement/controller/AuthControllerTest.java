package com.example.taskmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.auth.LoginRequestDto;
import com.example.taskmanagement.dto.auth.LoginResponseDto;
import com.example.taskmanagement.security.JwtUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authenticationManager, jwtUtil);
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUserId("admin");
        request.setPassword("admin123");
        UserDetails userDetails = new User("admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("jwt-token");

        ResponseEntity<LoginResponseDto> response = authController.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
    }

    @Test
    void loginReturnsUnauthorizedForBadCredentials() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUserId("admin");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<LoginResponseDto> response = authController.login(request);

        assertEquals(401, response.getStatusCode().value());
    }
}
