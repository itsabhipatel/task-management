package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
}
