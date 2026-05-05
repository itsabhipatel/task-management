package com.example.taskmanagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // http.csrf(AbstractHttpConfigurer::disable);
        // http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
        // http.httpBasic(AbstractHttpConfigurer::disable);
        // http.formLogin(AbstractHttpConfigurer::disable);
        // http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // http.exceptionHandling(exception -> exception
        //         .authenticationEntryPoint(customAuthenticationEntryPoint)
        //         .accessDeniedHandler(customAccessDeniedHandler));
        // http.authorizeHttpRequests(requests -> requests
        //         .requestMatchers("/api/auth/login", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()
        //         .requestMatchers("/api/tasks/page", "/api/tasks/sort", "/api/tasks/page-and-sort").hasRole("ADMIN")
        //         .anyRequest().hasAnyRole("USER", "ADMIN"));
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // return http.build();

        http
        .csrf(csrf -> csrf.disable()) // disable CSRF
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll() // allow all requests
        );

    return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
