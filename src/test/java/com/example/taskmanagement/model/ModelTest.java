package com.example.taskmanagement.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.dto.auth.LoginRequestDto;
import com.example.taskmanagement.dto.auth.LoginResponseDto;
import com.example.taskmanagement.entity.AppUser;
import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.entity.Task;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelTest {

    @Test
    void shouldSetAndGetTaskRequestFields() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Task");
        request.setStatus("TODO");
        request.setEmployeeId(1L);
        request.setCategoryId(2L);

        assertEquals("Task", request.getTitle());
        assertEquals("TODO", request.getStatus());
        assertEquals(1L, request.getEmployeeId());
        assertEquals(2L, request.getCategoryId());
    }

    @Test
    void shouldSetAndGetTaskResponseFields() {
        TaskResponseDto response = new TaskResponseDto();
        LocalDateTime createdDate = LocalDateTime.of(2026, 5, 13, 12, 0);
        response.setId(1L);
        response.setTitle("Task");
        response.setStatus("DONE");
        response.setCreatedDate(createdDate);
        response.setEmployeeId(2L);
        response.setEmployeeName("Abhi");
        response.setCategoryId(3L);
        response.setCategoryName("Development");

        assertEquals(1L, response.getId());
        assertEquals("Task", response.getTitle());
        assertEquals("DONE", response.getStatus());
        assertEquals(createdDate, response.getCreatedDate());
        assertEquals(2L, response.getEmployeeId());
        assertEquals("Abhi", response.getEmployeeName());
        assertEquals(3L, response.getCategoryId());
        assertEquals("Development", response.getCategoryName());
    }

    @Test
    void shouldSetAndGetLoginFields() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUserId("admin");
        request.setPassword("admin123");
        LoginResponseDto response = new LoginResponseDto("token");
        response.setToken("new-token");

        assertEquals("admin", request.getUserId());
        assertEquals("admin123", request.getPassword());
        assertEquals("new-token", response.getToken());
    }

    @Test
    void shouldSetAndGetEntityFields() {
        AppUser appUser = new AppUser();
        appUser.setUserId("user");
        appUser.setPassword("encoded");
        appUser.setRole("USER");

        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Neha");

        Category category = new Category();
        category.setId(2L);
        category.setName("Testing");

        Task task = new Task();
        task.setId(3L);
        task.setTitle("Task");
        task.setStatus("TODO");
        task.setEmployee(employee);
        task.setCategory(category);
        task.setCreatedDateBeforeSave();
        employee.setTasks(List.of(task));
        category.setTasks(List.of(task));

        assertEquals("user", appUser.getUserId());
        assertEquals("encoded", appUser.getPassword());
        assertEquals("USER", appUser.getRole());
        assertEquals(1L, employee.getId());
        assertEquals("Neha", employee.getName());
        assertEquals(2L, category.getId());
        assertEquals("Testing", category.getName());
        assertEquals(3L, task.getId());
        assertEquals("Task", task.getTitle());
        assertEquals("TODO", task.getStatus());
        assertSame(employee, task.getEmployee());
        assertSame(category, task.getCategory());
        assertEquals(List.of(task), employee.getTasks());
        assertEquals(List.of(task), category.getTasks());
        assertNotNull(task.getCreatedDate());
    }
}
