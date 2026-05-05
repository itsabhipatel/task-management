package com.example.taskmanagement.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        setPrivateField("secret", "taskmanagementjwtsecretkeymustbe32characterslong");
        setPrivateField("expiration", 3600000L);
        userDetails = new User("admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void generateTokenCreatesTokenForUser() {
        String token = jwtUtil.generateToken(userDetails);

        assertEquals("admin", jwtUtil.getUsernameFromToken(token));
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateTokenReturnsFalseForDifferentUser() {
        String token = jwtUtil.generateToken(userDetails);
        UserDetails otherUser = new User("user", "password", List.of(new SimpleGrantedAuthority("ROLE_USER")));

        assertFalse(jwtUtil.validateToken(token, otherUser));
    }

    @Test
    void invalidTokenThrowsException() {
        Executable executable = new Executable() {
            @Override
            public void execute() {
                jwtUtil.getUsernameFromToken("invalid-token");
            }
        };

        assertThrows(RuntimeException.class, executable);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        Field field = JwtUtil.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(jwtUtil, value);
    }
}
