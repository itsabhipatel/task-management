package com.example.taskmanagement.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() throws Exception {
        jwtUtil = new JwtUtil();
        setField("secret", "12345678901234567890123456789012");
        setField("expiration", 60_000L);
    }

    @Test
    void generatedTokenContainsUsernameAndValidates() {
        UserDetails user = new User("admin", "password", List.of());

        String token = jwtUtil.generateToken(user);

        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo("admin");
        assertThat(jwtUtil.validateToken(token, user)).isTrue();
        assertThat(jwtUtil.validateToken(token, new User("other", "password", List.of()))).isFalse();
    }

    private void setField(String name, Object value) throws Exception {
        Field field = JwtUtil.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(jwtUtil, value);
    }
}
