package com.example.taskmanagement.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.entity.AppUser;
import com.example.taskmanagement.repository.AppUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserWithPrefixedRole() {
        AppUser appUser = user("admin", "encoded", "ROLE_ADMIN");
        when(appUserRepository.findById("admin")).thenReturn(Optional.of(appUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertEquals("encoded", result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }

    @Test
    void shouldLoadUserWithUnprefixedRole() {
        AppUser appUser = user("user", "encoded", "USER");
        when(appUserRepository.findById("user")).thenReturn(Optional.of(appUser));

        UserDetails result = customUserDetailsService.loadUserByUsername("user");

        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_USER".equals(authority.getAuthority())));
    }

    @Test
    void shouldThrowWhenUserIsMissing() {
        when(appUserRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("missing"));
    }

    private AppUser user(String userId, String password, String role) {
        AppUser appUser = new AppUser();
        appUser.setUserId(userId);
        appUser.setPassword(password);
        appUser.setRole(role);
        return appUser;
    }
}
