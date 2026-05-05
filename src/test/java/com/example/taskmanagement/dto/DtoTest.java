package com.example.taskmanagement.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.taskmanagement.dto.auth.LoginRequestDto;
import com.example.taskmanagement.dto.auth.LoginResponseDto;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DtoTest {

    @Test
    void taskRequestDtoStoresValues() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Build API");
        request.setStatus("TODO");
        request.setEmployeeId(1L);
        request.setCategoryId(2L);

        assertEquals("Build API", request.getTitle());
        assertEquals("TODO", request.getStatus());
        assertEquals(1L, request.getEmployeeId());
        assertEquals(2L, request.getCategoryId());
    }

    @Test
    void taskResponseDtoStoresValues() {
        LocalDateTime createdDate = LocalDateTime.of(2026, 5, 5, 10, 0);
        TaskResponseDto response = new TaskResponseDto();
        response.setId(1L);
        response.setTitle("Build API");
        response.setStatus("TODO");
        response.setCreatedDate(createdDate);
        response.setEmployeeId(2L);
        response.setEmployeeName("Abhi Patel");
        response.setCategoryId(3L);
        response.setCategoryName("Development");

        assertEquals(1L, response.getId());
        assertEquals("Build API", response.getTitle());
        assertEquals("TODO", response.getStatus());
        assertEquals(createdDate, response.getCreatedDate());
        assertEquals(2L, response.getEmployeeId());
        assertEquals("Abhi Patel", response.getEmployeeName());
        assertEquals(3L, response.getCategoryId());
        assertEquals("Development", response.getCategoryName());
    }

    @Test
    void loginDtosStoreValues() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUserId("admin");
        request.setPassword("admin123");

        LoginResponseDto response = new LoginResponseDto("token");
        response.setToken("new-token");

        assertEquals("admin", request.getUserId());
        assertEquals("admin123", request.getPassword());
        assertEquals("new-token", response.getToken());
    }
}
