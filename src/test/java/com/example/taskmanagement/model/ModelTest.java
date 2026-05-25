package com.example.taskmanagement.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.example.taskmanagement.dto.ApiErrorResponse;
import com.example.taskmanagement.dto.FilterDto;
import com.example.taskmanagement.dto.NotificationRequestDto;
import com.example.taskmanagement.dto.NotificationResponseDto;
import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.dto.TaskSearchRequestDto;
import com.example.taskmanagement.dto.TaskSummaryDto;
import com.example.taskmanagement.dto.auth.LoginRequestDto;
import com.example.taskmanagement.dto.auth.LoginResponseDto;
import com.example.taskmanagement.entity.AppUser;
import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.entity.Task;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModelTest {

    @Test
    void shouldSetAndGetTaskRequestFields() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Task");
        request.setDescription("Details");
        request.setStatus("TODO");
        request.setPriority("HIGH");
        LocalDateTime dueDate = LocalDateTime.of(2026, 5, 20, 12, 0);
        request.setDueDate(dueDate);
        request.setProgressPercentage(40);
        request.setEmployeeId(1L);
        request.setCategoryId(2L);

        assertEquals("Task", request.getTitle());
        assertEquals("Details", request.getDescription());
        assertEquals("TODO", request.getStatus());
        assertEquals("HIGH", request.getPriority());
        assertEquals(dueDate, request.getDueDate());
        assertEquals(40, request.getProgressPercentage());
        assertEquals(1L, request.getEmployeeId());
        assertEquals(2L, request.getCategoryId());
    }

    @Test
    void shouldSetAndGetTaskResponseFields() {
        TaskResponseDto response = new TaskResponseDto();
        LocalDateTime createdDate = LocalDateTime.of(2026, 5, 13, 12, 0);
        LocalDateTime updatedDate = LocalDateTime.of(2026, 5, 14, 12, 0);
        LocalDateTime completedDate = LocalDateTime.of(2026, 5, 15, 12, 0);
        LocalDateTime dueDate = LocalDateTime.of(2026, 5, 16, 12, 0);
        response.setId(1L);
        response.setTitle("Task");
        response.setDescription("Details");
        response.setStatus("DONE");
        response.setPriority("HIGH");
        response.setDueDate(dueDate);
        response.setCreatedDate(createdDate);
        response.setUpdatedDate(updatedDate);
        response.setCompletedDate(completedDate);
        response.setProgressPercentage(100);
        response.setOverdue(false);
        response.setEmployeeId(2L);
        response.setEmployeeName("Abhi");
        response.setCategoryId(3L);
        response.setCategoryName("Development");

        assertEquals(1L, response.getId());
        assertEquals("Task", response.getTitle());
        assertEquals("Details", response.getDescription());
        assertEquals("DONE", response.getStatus());
        assertEquals("HIGH", response.getPriority());
        assertEquals(dueDate, response.getDueDate());
        assertEquals(createdDate, response.getCreatedDate());
        assertEquals(updatedDate, response.getUpdatedDate());
        assertEquals(completedDate, response.getCompletedDate());
        assertEquals(100, response.getProgressPercentage());
        assertEquals(false, response.isOverdue());
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
    void shouldSetAndGetTaskSummaryFields() {
        TaskSummaryDto summary = new TaskSummaryDto();
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        statusCounts.put("TODO", 2L);
        Map<String, Long> priorityCounts = new LinkedHashMap<>();
        priorityCounts.put("HIGH", 1L);

        summary.setTotalTasks(3);
        summary.setOverdueTasks(1);
        summary.setAverageProgress(55.5);
        summary.setStatusCounts(statusCounts);
        summary.setPriorityCounts(priorityCounts);

        assertEquals(3, summary.getTotalTasks());
        assertEquals(1, summary.getOverdueTasks());
        assertEquals(55.5, summary.getAverageProgress());
        assertEquals(statusCounts, summary.getStatusCounts());
        assertEquals(priorityCounts, summary.getPriorityCounts());
    }

    @Test
    void shouldSetAndGetTaskSearchFields() {
        TaskSearchRequestDto request = new TaskSearchRequestDto();
        FilterDto filter = new FilterDto();
        filter.setField("status");
        filter.setOperator("eq");
        filter.setValue("TODO");

        request.setFilters(List.of(filter));
        request.setSortBy(List.of("title"));
        request.setSortDirection(List.of("asc"));
        request.setPage(0);
        request.setSize(10);

        assertEquals(List.of(filter), request.getFilters());
        assertEquals(List.of("title"), request.getSortBy());
        assertEquals(List.of("asc"), request.getSortDirection());
        assertEquals(0, request.getPage());
        assertEquals(10, request.getSize());
        assertEquals("status", filter.getField());
        assertEquals("eq", filter.getOperator());
        assertEquals("TODO", filter.getValue());
    }

    @Test
    void shouldSetAndGetApiErrorResponseFields() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 5, 19, 12, 0);
        ApiErrorResponse error = new ApiErrorResponse(timestamp, 400, "Bad Request", "Invalid input", "/api/tasks");
        LocalDateTime newTimestamp = LocalDateTime.of(2026, 5, 19, 13, 0);

        error.setTimestamp(newTimestamp);
        error.setStatus(404);
        error.setError("Not Found");
        error.setMessage("Task missing");
        error.setPath("/api/tasks/1");

        assertEquals(newTimestamp, error.getTimestamp());
        assertEquals(404, error.getStatus());
        assertEquals("Not Found", error.getError());
        assertEquals("Task missing", error.getMessage());
        assertEquals("/api/tasks/1", error.getPath());
    }

    @Test
    void shouldSetAndGetNotificationFields() {
        LocalDateTime sentAt = LocalDateTime.of(2026, 5, 25, 11, 0);
        NotificationRequestDto request = new NotificationRequestDto(1L, "Task", "Created");
        NotificationResponseDto response = new NotificationResponseDto("SENT", "Done", sentAt);

        request.setTaskId(2L);
        request.setTitle("Updated task");
        request.setMessage("Updated");
        response.setStatus("QUEUED");
        response.setMessage("Queued");
        response.setSentAt(sentAt.plusHours(1));

        assertEquals(2L, request.getTaskId());
        assertEquals("Updated task", request.getTitle());
        assertEquals("Updated", request.getMessage());
        assertEquals("QUEUED", response.getStatus());
        assertEquals("Queued", response.getMessage());
        assertEquals(sentAt.plusHours(1), response.getSentAt());
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
        task.setDescription("Details");
        task.setStatus("TODO");
        task.setPriority("MEDIUM");
        task.setDueDate(LocalDateTime.of(2026, 5, 20, 12, 0));
        task.setProgressPercentage(30);
        task.setEmployee(employee);
        task.setCategory(category);
        task.setCreatedDateBeforeSave();
        task.setCompletedDate(LocalDateTime.of(2026, 5, 21, 12, 0));
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
        assertEquals("Details", task.getDescription());
        assertEquals("TODO", task.getStatus());
        assertEquals("MEDIUM", task.getPriority());
        assertEquals(30, task.getProgressPercentage());
        assertSame(employee, task.getEmployee());
        assertSame(category, task.getCategory());
        assertEquals(List.of(task), employee.getTasks());
        assertEquals(List.of(task), category.getTasks());
        assertNotNull(task.getCreatedDate());
        assertNotNull(task.getUpdatedDate());
        assertNotNull(task.getCompletedDate());
    }

    @Test
    void shouldBuildTaskUsingBuilderPattern() {
        Employee employee = new Employee();
        employee.setId(1L);
        Category category = new Category();
        category.setId(2L);
        LocalDateTime dueDate = LocalDateTime.of(2026, 5, 20, 12, 0);

        Task task = Task.builder()
                .id(3L)
                .title("Task")
                .description("Details")
                .status("TODO")
                .priority("HIGH")
                .dueDate(dueDate)
                .progressPercentage(40)
                .employee(employee)
                .category(category)
                .build();

        assertEquals(3L, task.getId());
        assertEquals("Task", task.getTitle());
        assertEquals("Details", task.getDescription());
        assertEquals("TODO", task.getStatus());
        assertEquals("HIGH", task.getPriority());
        assertEquals(dueDate, task.getDueDate());
        assertEquals(40, task.getProgressPercentage());
        assertSame(employee, task.getEmployee());
        assertSame(category, task.getCategory());
    }
}
