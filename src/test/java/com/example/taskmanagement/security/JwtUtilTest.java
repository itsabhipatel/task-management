package com.example.taskmanagement.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        setField("secret", "12345678901234567890123456789012");
        setField("expiration", 60_000L);
    }

    @Test
    void shouldGenerateTokenAndReadUsername() {
        UserDetails user = user("admin");

        String token = jwtUtil.generateToken(user);

        assertEquals("admin", jwtUtil.getUsernameFromToken(token));
        assertTrue(jwtUtil.validateToken(token, user));
    }

    @Test
    void shouldRejectTokenForDifferentUser() {
        String token = jwtUtil.generateToken(user("admin"));

        boolean result = jwtUtil.validateToken(token, user("other"));

        assertFalse(result);
    }

    private UserDetails user(String username) {
        return new SimpleUserDetails(username);
    }

    private void setField(String name, Object value) throws Exception {
        Field field = JwtUtil.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(jwtUtil, value);
    }

    private static class SimpleUserDetails implements UserDetails {

        private final String username;

        SimpleUserDetails(String username) {
            this.username = username;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return List.of();
        }

        @Override
        public String getPassword() {
            return "password";
        }

        @Override
        public String getUsername() {
            return username;
        }
    }
}
