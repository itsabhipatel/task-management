package com.example.taskmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.BulkStatusUpdateDto;
import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.dto.TaskSummaryDto;
import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.CategoryRepository;
import com.example.taskmanagement.repository.EmployeeRepository;
import com.example.taskmanagement.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldReturnAllTasks() {
        Task task = task(1L, "Build API", "TODO", employee(2L, "Abhi"), category(3L, "Development"));
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Build API", result.get(0).getTitle());
        assertEquals("Abhi", result.get(0).getEmployeeName());
        assertEquals("Development", result.get(0).getCategoryName());
    }

    @Test
    void shouldReturnTaskById() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task(1L, "Task", "DONE", null, null)));

        TaskResponseDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("DONE", result.getStatus());
    }

    @Test
    void shouldReturnNullWhenTaskDoesNotExist() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        TaskResponseDto result = taskService.getTaskById(99L);

        assertNull(result);
    }

    @Test
    void shouldReturnTasksByStatus() {
        when(taskRepository.findByStatus("TODO")).thenReturn(List.of(task(1L, "Task", "TODO", null, null)));

        List<TaskResponseDto> result = taskService.getTasksByStatus("TODO");

        assertEquals(1, result.size());
        assertEquals("TODO", result.get(0).getStatus());
    }

    @Test
    void shouldReturnSortedTasks() {
        when(taskRepository.findAll(Sort.by("title"))).thenReturn(List.of(task(1L, "A Task", "TODO", null, null)));

        List<TaskResponseDto> result = taskService.getSortedTasks(List.of("title"));

        assertEquals(1, result.size());
        assertEquals("A Task", result.get(0).getTitle());
    }

    @Test
    void shouldReturnPaginatedTasks() {
        Pageable pageable = PageRequest.of(0, 2);
        when(taskRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(task(1L, "Task", "TODO", null, null))));

        List<TaskResponseDto> result = taskService.getPaginatedTasks(pageable);

        assertEquals(1, result.size());
        assertEquals("Task", result.get(0).getTitle());
    }

    @Test
    void shouldReturnPaginatedAndSortedTasks() {
        Pageable pageable = PageRequest.of(1, 3, Sort.by("status"));
        when(taskRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(task(1L, "Task", "DONE", null, null))));

        List<TaskResponseDto> result = taskService.getPaginatedAndSortedTasks(1, 3, List.of("status"));

        assertEquals(1, result.size());
        assertEquals("DONE", result.get(0).getStatus());
    }

    @Test
    void shouldCreateTask() {
        TaskRequestDto request = request("New task", "TODO", 1L, 2L);
        request.setDescription("Details");
        request.setPriority("high");
        request.setDueDate(LocalDateTime.of(2026, 5, 20, 10, 0));
        request.setProgressPercentage(25);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee(1L, "Neha")));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category(2L, "Testing")));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            savedTask.setId(10L);
            return savedTask;
        });

        TaskResponseDto result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("New task", result.getTitle());
        assertEquals(1L, result.getEmployeeId());
        assertEquals(2L, result.getCategoryId());
        assertEquals("Details", result.getDescription());
        assertEquals("HIGH", result.getPriority());
        assertEquals(25, result.getProgressPercentage());
    }

    @Test
    void shouldCreateTaskWithoutOptionalEmployeeAndCategory() {
        TaskRequestDto request = request("New task", "TODO", null, null);
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskResponseDto result = taskService.createTask(request);

        assertEquals("New task", result.getTitle());
        assertNull(result.getEmployeeId());
        assertNull(result.getCategoryId());
    }

    @Test
    void shouldUpdateTask() {
        Task existingTask = task(5L, "Old", "TODO", null, null);
        when(taskRepository.findById(5L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskResponseDto result = taskService.updateTask(5L, request("Updated", "DONE", null, null));

        assertNotNull(result);
        assertEquals("Updated", result.getTitle());
        assertEquals("DONE", result.getStatus());
        assertEquals(100, result.getProgressPercentage());
        assertNotNull(result.getCompletedDate());
    }

    @Test
    void shouldReturnNullWhenUpdatingMissingTask() {
        when(taskRepository.findById(5L)).thenReturn(Optional.empty());

        TaskResponseDto result = taskService.updateTask(5L, request("Updated", "DONE", null, null));

        assertNull(result);
    }

    @Test
    void shouldReturnTaskSummary() {
        Task overdue = task(1L, "Late", "TODO", null, null);
        overdue.setPriority("HIGH");
        overdue.setDueDate(LocalDateTime.of(2020, 1, 1, 10, 0));
        overdue.setProgressPercentage(20);
        Task done = task(2L, "Done", "DONE", null, null);
        done.setPriority("LOW");
        done.setDueDate(LocalDateTime.of(2020, 1, 1, 10, 0));
        done.setProgressPercentage(100);
        when(taskRepository.findAll()).thenReturn(List.of(overdue, done));

        TaskSummaryDto result = taskService.getTaskSummary();

        assertEquals(2, result.getTotalTasks());
        assertEquals(1, result.getOverdueTasks());
        assertEquals(60, result.getAverageProgress());
        assertEquals(1, result.getStatusCounts().get("TODO"));
        assertEquals(1, result.getPriorityCounts().get("HIGH"));
    }

    @Test
    void shouldReturnOverdueTasks() {
        Task overdue = task(1L, "Late", "TODO", null, null);
        when(taskRepository.findByDueDateBeforeAndStatusNot(any(LocalDateTime.class), any()))
                .thenReturn(List.of(overdue));

        List<TaskResponseDto> result = taskService.getOverdueTasks();

        assertEquals(1, result.size());
        assertEquals("Late", result.get(0).getTitle());
        assertTrue(result.get(0).isOverdue());
    }

    @Test
    void shouldBulkUpdateTaskStatus() {
        BulkStatusUpdateDto request = bulkStatusRequest(List.of(1L, 2L), "DONE");
        Task firstTask = task(1L, "First", "TODO", null, null);
        Task secondTask = task(2L, "Second", "IN_PROGRESS", null, null);
        when(taskRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(firstTask, secondTask));
        when(taskRepository.saveAll(List.of(firstTask, secondTask))).thenReturn(List.of(firstTask, secondTask));

        List<TaskResponseDto> result = taskService.bulkUpdateTaskStatus(request);

        assertEquals(2, result.size());
        assertEquals("DONE", result.get(0).getStatus());
        assertEquals(100, result.get(0).getProgressPercentage());
        assertNotNull(result.get(0).getCompletedDate());
        assertEquals("DONE", result.get(1).getStatus());
    }

    @Test
    void shouldRejectBulkStatusUpdateWithoutTaskIds() {
        BulkStatusUpdateDto request = bulkStatusRequest(List.of(), "DONE");

        assertThrows(IllegalArgumentException.class, () -> taskService.bulkUpdateTaskStatus(request));
    }

    @Test
    void shouldUpdateProgressAndCompleteTaskAtOneHundredPercent() {
        Task existingTask = task(5L, "Task", "IN_PROGRESS", null, null);
        when(taskRepository.findById(5L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskResponseDto result = taskService.updateTaskProgress(5L, 100);

        assertNotNull(result);
        assertEquals("DONE", result.getStatus());
        assertEquals(100, result.getProgressPercentage());
        assertNotNull(result.getCompletedDate());
    }

    @Test
    void shouldReturnNullWhenUpdatingProgressForMissingTask() {
        when(taskRepository.findById(5L)).thenReturn(Optional.empty());

        TaskResponseDto result = taskService.updateTaskProgress(5L, 50);

        assertNull(result);
    }

    @Test
    void shouldDeleteTask() {
        Task task = task(5L, "Task", "TODO", null, null);
        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        boolean result = taskService.deleteTask(5L);

        assertTrue(result);
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldReturnFalseWhenDeletingMissingTask() {
        when(taskRepository.findById(5L)).thenReturn(Optional.empty());

        boolean result = taskService.deleteTask(5L);

        assertFalse(result);
    }

    private TaskRequestDto request(String title, String status, Long employeeId, Long categoryId) {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle(title);
        request.setStatus(status);
        request.setEmployeeId(employeeId);
        request.setCategoryId(categoryId);
        return request;
    }

    private BulkStatusUpdateDto bulkStatusRequest(List<Long> taskIds, String status) {
        BulkStatusUpdateDto request = new BulkStatusUpdateDto();
        request.setTaskIds(taskIds);
        request.setStatus(status);
        return request;
    }

    private Task task(Long id, String title, String status, Employee employee, Category category) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setDescription("Description");
        task.setStatus(status);
        task.setPriority("MEDIUM");
        task.setProgressPercentage("DONE".equals(status) ? 100 : 0);
        task.setDueDate(LocalDateTime.of(2020, 1, 1, 10, 0));
        if ("DONE".equals(status)) {
            task.setCompletedDate(LocalDateTime.of(2026, 5, 13, 11, 0));
        }
        task.setCreatedDate(LocalDateTime.of(2026, 5, 13, 10, 0));
        task.setEmployee(employee);
        task.setCategory(category);
        return task;
    }

    private Employee employee(Long id, String name) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setName(name);
        return employee;
    }

    private Category category(Long id, String name) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }

}
