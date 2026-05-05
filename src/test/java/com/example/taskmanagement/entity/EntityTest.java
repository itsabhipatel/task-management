package com.example.taskmanagement.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class EntityTest {

    @Test
    void appUserStoresValues() {
        AppUser appUser = new AppUser();
        appUser.setUserId("admin");
        appUser.setPassword("encoded");
        appUser.setRole("ADMIN");

        assertEquals("admin", appUser.getUserId());
        assertEquals("encoded", appUser.getPassword());
        assertEquals("ADMIN", appUser.getRole());
    }

    @Test
    void categoryAndEmployeeStoreTasks() {
        Task task = new Task();
        Category category = new Category();
        Employee employee = new Employee();

        category.setId(1L);
        category.setName("Development");
        category.setTasks(List.of(task));
        employee.setId(2L);
        employee.setName("Abhi Patel");
        employee.setTasks(List.of(task));

        assertEquals(1L, category.getId());
        assertEquals("Development", category.getName());
        assertSame(task, category.getTasks().get(0));
        assertEquals(2L, employee.getId());
        assertEquals("Abhi Patel", employee.getName());
        assertSame(task, employee.getTasks().get(0));
    }

    @Test
    void taskStoresValues() {
        Employee employee = new Employee();
        Category category = new Category();
        LocalDateTime createdDate = LocalDateTime.of(2026, 5, 5, 10, 0);
        Task task = new Task();

        task.setId(1L);
        task.setTitle("Build API");
        task.setStatus("TODO");
        task.setCreatedDate(createdDate);
        task.setEmployee(employee);
        task.setCategory(category);

        assertEquals(1L, task.getId());
        assertEquals("Build API", task.getTitle());
        assertEquals("TODO", task.getStatus());
        assertEquals(createdDate, task.getCreatedDate());
        assertSame(employee, task.getEmployee());
        assertSame(category, task.getCategory());
    }

    @Test
    void taskPrePersistSetsCreatedDate() {
        Task task = new Task();

        task.setCreatedDateBeforeSave();

        assertNotNull(task.getCreatedDate());
    }
}
