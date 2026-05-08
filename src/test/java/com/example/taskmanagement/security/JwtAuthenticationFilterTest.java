package com.example.taskmanagement.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private FilterChain filterChain;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void continuesWithoutAuthenticationWhenHeaderIsMissing() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, authenticationEntryPoint);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void authenticatesValidBearerToken() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, authenticationEntryPoint);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        UserDetails userDetails = User.withUsername("admin").password("password").roles("ADMIN").build();
        when(jwtUtil.getUsernameFromToken("token")).thenReturn("admin");
        when(customUserDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
        when(jwtUtil.validateToken("token", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("admin");
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void sendsInvalidTokenToEntryPointAndStopsChain() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil, customUserDetailsService, authenticationEntryPoint);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer bad");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtUtil.getUsernameFromToken("bad")).thenThrow(new IllegalArgumentException("bad"));

        filter.doFilterInternal(request, response, filterChain);

        verify(authenticationEntryPoint).commence(org.mockito.ArgumentMatchers.eq(request),
                org.mockito.ArgumentMatchers.eq(response), org.mockito.ArgumentMatchers.any());
        verify(filterChain, never()).doFilter(request, response);
    }
}
