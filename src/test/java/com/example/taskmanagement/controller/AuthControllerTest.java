package com.example.taskmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.taskmanagement.dto.auth.LoginRequestDto;
import com.example.taskmanagement.dto.auth.LoginResponseDto;
import com.example.taskmanagement.security.JwtUtil;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Test
    void shouldReturnTokenWhenCredentialsAreValid() {
        AuthController authController = new AuthController(new SuccessfulAuthenticationManager(), new FixedJwtUtil());

        ResponseEntity<LoginResponseDto> response = authController.login(request("admin", "admin123"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
    }

    @Test
    void shouldReturnUnauthorizedWhenCredentialsAreInvalid() {
        AuthController authController = new AuthController(new FailedAuthenticationManager(), new FixedJwtUtil());

        ResponseEntity<LoginResponseDto> response = authController.login(request("admin", "wrong"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private LoginRequestDto request(String userId, String password) {
        LoginRequestDto request = new LoginRequestDto();
        request.setUserId(userId);
        request.setPassword(password);
        return request;
    }

    private static class SuccessfulAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication authentication) {
            return new SuccessfulAuthentication();
        }
    }

    private static class FailedAuthenticationManager implements AuthenticationManager {

        @Override
        public Authentication authenticate(Authentication authentication) {
            throw new BadCredentialsException("Invalid credentials");
        }
    }

    private static class SuccessfulAuthentication implements Authentication {

        private boolean authenticated = true;

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of();
        }

        @Override
        public Object getCredentials() {
            return null;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return new SimpleUserDetails();
        }

        @Override
        public boolean isAuthenticated() {
            return authenticated;
        }

        @Override
        public void setAuthenticated(boolean authenticated) {
            this.authenticated = authenticated;
        }

        @Override
        public String getName() {
            return "admin";
        }
    }

    private static class SimpleUserDetails implements UserDetails {

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of();
        }

        @Override
        public String getPassword() {
            return "encoded";
        }

        @Override
        public String getUsername() {
            return "admin";
        }
    }

    private static class FixedJwtUtil extends JwtUtil {

        @Override
        public String generateToken(UserDetails userDetails) {
            return "jwt-token";
        }
    }
}
