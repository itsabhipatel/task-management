package com.example.taskmanagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.auth.LoginRequestDto;
import com.example.taskmanagement.dto.auth.LoginResponseDto;
import com.example.taskmanagement.security.JwtUtil;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    @InjectMocks
    private AuthController authController;

    @Test
    void loginReturnsTokenWhenCredentialsAreValid() {
        LoginRequestDto request = request();
        UserDetails user = new User("admin", "secret", List.of());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<LoginResponseDto> response = authController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getToken()).isEqualTo("jwt-token");
    }

    @Test
    void loginReturnsUnauthorizedWhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThat(authController.login(request()).getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private LoginRequestDto request() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUserId("admin");
        request.setPassword("admin123");
        return request;
    }
}
