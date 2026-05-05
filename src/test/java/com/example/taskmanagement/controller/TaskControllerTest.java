package com.example.taskmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.service.TaskService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    private TaskController taskController;

    @BeforeEach
    void setUp() {
        taskController = new TaskController(taskService);
    }

    @Test
    void getAllTasksReturnsTasks() {
        when(taskService.getAllTasks()).thenReturn(List.of(taskResponse()));

        List<TaskResponseDto> response = taskController.getAllTasks();

        assertEquals(1, response.size());
        assertEquals("Build API", response.get(0).getTitle());
    }

    @Test
    void getPaginatedTasksReturnsPage() {
        when(taskService.getPaginatedTasks(any())).thenReturn(new PageImpl<TaskResponseDto>(List.of(taskResponse())));

        Page<TaskResponseDto> response = taskController.getPaginatedTasks(0, 5);

        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getSortedTasksReturnsTasks() {
        when(taskService.getSortedTasks(List.of("title"))).thenReturn(List.of(taskResponse()));

        List<TaskResponseDto> response = taskController.getSortedTasks(List.of("title"));

        assertEquals("Build API", response.get(0).getTitle());
    }

    @Test
    void getPaginatedAndSortedTasksReturnsPage() {
        when(taskService.getPaginatedAndSortedTasks(0, 5, List.of("title")))
                .thenReturn(new PageImpl<TaskResponseDto>(List.of(taskResponse())));

        Page<TaskResponseDto> response =
                taskController.getPaginatedAndSortedTasks(0, 5, List.of("title"));

        assertEquals(1, response.getTotalElements());
    }

    @Test
    void getTaskByIdReturnsOkWhenFound() {
        when(taskService.getTaskById(1L)).thenReturn(taskResponse());

        ResponseEntity<TaskResponseDto> response = taskController.getTaskById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void getTaskByIdReturnsNotFoundWhenMissing() {
        when(taskService.getTaskById(1L)).thenReturn(null);

        ResponseEntity<TaskResponseDto> response = taskController.getTaskById(1L);

        assertEquals(404, response.getStatusCode().value());
        assertNull(response.getBody());
    }

    @Test
    void createTaskReturnsCreatedTask() {
        TaskRequestDto request = taskRequest();
        when(taskService.createTask(request)).thenReturn(taskResponse());

        TaskResponseDto response = taskController.createTask(request);

        assertEquals("Build API", response.getTitle());
    }

    @Test
    void updateTaskReturnsOkWhenFound() {
        TaskRequestDto request = taskRequest();
        when(taskService.updateTask(1L, request)).thenReturn(taskResponse());

        ResponseEntity<TaskResponseDto> response = taskController.updateTask(1L, request);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    void deleteTaskReturnsNoContentWhenDeleted() {
        when(taskService.deleteTask(1L)).thenReturn(true);

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(taskService).deleteTask(1L);
    }

    private TaskRequestDto taskRequest() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Build API");
        request.setStatus("TODO");
        request.setEmployeeId(1L);
        request.setCategoryId(1L);
        return request;
    }

    private TaskResponseDto taskResponse() {
        TaskResponseDto response = new TaskResponseDto();
        response.setId(1L);
        response.setTitle("Build API");
        response.setStatus("TODO");
        return response;
    }
}
