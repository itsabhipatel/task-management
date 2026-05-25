package com.example.taskmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.BulkStatusUpdateDto;
import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.dto.TaskSearchRequestDto;
import com.example.taskmanagement.dto.TaskSummaryDto;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.service.TaskService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @Test
    void shouldReturnAllTasks() {
        TaskResponseDto task = response(1L, "Task");
        when(taskService.getAllTasks()).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.getAllTasks();

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldReturnTaskById() {
        TaskResponseDto task = response(1L, "Task");
        when(taskService.getTaskById(1L)).thenReturn(task);

        var result = taskController.getTaskById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(task, result.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenTaskByIdIsMissing() {
        when(taskService.getTaskById(1L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> taskController.getTaskById(1L));
    }

    @Test
    void shouldReturnPaginatedTasks() {
        TaskResponseDto task = response(1L, "Task");
        when(taskService.getPaginatedTasks(any(Pageable.class))).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.getPaginatedTasks(0, 5);

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldReturnTasksByStatus() {
        TaskResponseDto task = response(1L, "Task");
        when(taskService.getTasksByStatus("TODO")).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.getTasksByStatus("TODO");

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldReturnSortedTasks() {
        TaskResponseDto task = response(1L, "Task");
        when(taskService.getSortedTasks(List.of("title"))).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.getSortedTasks(List.of("title"));

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldReturnPaginatedAndSortedTasks() {
        TaskResponseDto task = response(1L, "Task");
        when(taskService.getPaginatedAndSortedTasks(0, 5, List.of("id"))).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.getPaginatedAndSortedTasks(0, 5, List.of("id"));

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldSearchTasks() throws Exception {
        TaskSearchRequestDto request = new TaskSearchRequestDto();
        TaskResponseDto task = response(1L, "Task");
        when(taskService.filterTasks(request)).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.searchTasks(request);

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldReturnTaskSummary() {
        TaskSummaryDto summary = new TaskSummaryDto();
        summary.setTotalTasks(10);
        when(taskService.getTaskSummary()).thenReturn(summary);

        TaskSummaryDto result = taskController.getTaskSummary();

        assertSame(summary, result);
        assertEquals(10, result.getTotalTasks());
    }

    @Test
    void shouldReturnOverdueTasks() {
        TaskResponseDto task = response(1L, "Late task");
        when(taskService.getOverdueTasks()).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.getOverdueTasks();

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldCreateTask() {
        TaskRequestDto request = new TaskRequestDto();
        TaskResponseDto task = response(1L, "Task");
        when(taskService.createTask(request)).thenReturn(task);

        TaskResponseDto result = taskController.createTask(request);

        assertSame(task, result);
    }

    @Test
    void shouldUpdateTask() {
        TaskRequestDto request = new TaskRequestDto();
        TaskResponseDto task = response(1L, "Task");
        when(taskService.updateTask(1L, request)).thenReturn(task);

        var result = taskController.updateTask(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(task, result.getBody());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingMissingTask() {
        TaskRequestDto request = new TaskRequestDto();
        when(taskService.updateTask(1L, request)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> taskController.updateTask(1L, request));
    }

    @Test
    void shouldBulkUpdateTaskStatus() {
        BulkStatusUpdateDto request = new BulkStatusUpdateDto();
        TaskResponseDto task = response(1L, "Task");
        when(taskService.bulkUpdateTaskStatus(request)).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskController.bulkUpdateTaskStatus(request);

        assertEquals(1, result.size());
        assertSame(task, result.get(0));
    }

    @Test
    void shouldDeleteTask() {
        when(taskService.deleteTask(1L)).thenReturn(true);

        var result = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundWhenDeletingMissingTask() {
        when(taskService.deleteTask(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskController.deleteTask(1L));
    }

    private TaskResponseDto response(Long id, String title) {
        TaskResponseDto response = new TaskResponseDto();
        response.setId(id);
        response.setTitle(title);
        return response;
    }
}
