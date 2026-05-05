package com.example.taskmanagement.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void filterSetsAuthenticationForValidToken() throws Exception {
        UserDetails userDetails = new User("admin", "password", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.getUsernameFromToken("valid-token")).thenReturn("admin");
        when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken("valid-token", userDetails)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("admin", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void filterContinuesWhenAuthorizationHeaderIsMissing() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(jwtUtil, never()).getUsernameFromToken("valid-token");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void filterReturnsUnauthorizedForInvalidToken() throws Exception {
        StringWriter writer = new StringWriter();
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtil.getUsernameFromToken("invalid-token")).thenThrow(new RuntimeException("bad token"));
        when(response.getWriter()).thenReturn(new PrintWriter(writer));

        filter.doFilter(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertEquals("{\"message\":\"Invalid or expired JWT token.\"}", writer.toString());
        verify(filterChain, never()).doFilter(request, response);
    }
}
