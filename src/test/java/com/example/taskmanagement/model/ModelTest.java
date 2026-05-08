package com.example.taskmanagement.model;

import static org.assertj.core.api.Assertions.assertThat;

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

class ModelTest {

    @Test
    void dtoGettersAndSettersWork() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Title");
        request.setStatus("TODO");
        request.setEmployeeId(1L);
        request.setCategoryId(2L);

        TaskResponseDto response = new TaskResponseDto();
        LocalDateTime created = LocalDateTime.of(2026, 5, 9, 12, 0);
        response.setId(3L);
        response.setTitle(request.getTitle());
        response.setStatus(request.getStatus());
        response.setCreatedDate(created);
        response.setEmployeeId(request.getEmployeeId());
        response.setEmployeeName("Abhi");
        response.setCategoryId(request.getCategoryId());
        response.setCategoryName("Dev");

        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUserId("admin");
        loginRequest.setPassword("password");
        LoginResponseDto loginResponse = new LoginResponseDto("token");
        loginResponse.setToken("new-token");

        assertThat(response.getTitle()).isEqualTo("Title");
        assertThat(response.getStatus()).isEqualTo("TODO");
        assertThat(response.getCreatedDate()).isEqualTo(created);
        assertThat(response.getEmployeeId()).isEqualTo(1L);
        assertThat(response.getEmployeeName()).isEqualTo("Abhi");
        assertThat(response.getCategoryId()).isEqualTo(2L);
        assertThat(response.getCategoryName()).isEqualTo("Dev");
        assertThat(loginRequest.getUserId()).isEqualTo("admin");
        assertThat(loginRequest.getPassword()).isEqualTo("password");
        assertThat(loginResponse.getToken()).isEqualTo("new-token");
    }

    @Test
    void entityGettersSettersAndPrePersistWork() {
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

        assertThat(appUser.getUserId()).isEqualTo("user");
        assertThat(appUser.getPassword()).isEqualTo("encoded");
        assertThat(appUser.getRole()).isEqualTo("USER");
        assertThat(employee.getTasks()).containsExactly(task);
        assertThat(category.getTasks()).containsExactly(task);
        assertThat(task.getId()).isEqualTo(3L);
        assertThat(task.getTitle()).isEqualTo("Task");
        assertThat(task.getStatus()).isEqualTo("TODO");
        assertThat(task.getEmployee()).isSameAs(employee);
        assertThat(task.getCategory()).isSameAs(category);
        assertThat(task.getCreatedDate()).isNotNull();
    }
}
