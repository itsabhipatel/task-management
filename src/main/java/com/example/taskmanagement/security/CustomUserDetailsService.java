package com.example.taskmanagement.security;

import com.example.taskmanagement.entity.AppUser;
import com.example.taskmanagement.repository.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    public CustomUserDetailsService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findById(username).orElse(null);

        if (appUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        String role = appUser.getRole();
        String authority = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return User.withUsername(appUser.getUserId())
                .password(appUser.getPassword())
                .authorities(authority)
                .build();
    }
}
