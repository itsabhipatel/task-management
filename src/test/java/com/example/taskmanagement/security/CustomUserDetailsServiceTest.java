package com.example.taskmanagement.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.entity.AppUser;
import com.example.taskmanagement.repository.AppUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(appUserRepository);
    }

    @Test
    void loadUserByUsernameReturnsUserDetails() {
        AppUser appUser = new AppUser();
        appUser.setUserId("admin");
        appUser.setPassword("encoded-password");
        appUser.setRole("ADMIN");
        when(appUserRepository.findById("admin")).thenReturn(Optional.of(appUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        assertEquals("admin", userDetails.getUsername());
        assertEquals("encoded-password", userDetails.getPassword());
        assertEquals("ROLE_ADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        when(appUserRepository.findById("missing")).thenReturn(Optional.empty());

        Executable executable = new Executable() {
            @Override
            public void execute() {
                userDetailsService.loadUserByUsername("missing");
            }
        };

        assertThrows(UsernameNotFoundException.class, executable);
    }
}
