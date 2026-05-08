package com.example.taskmanagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.service.TaskService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
    void delegatesListEndpoints() {
        TaskResponseDto dto = dto(1L, "Task");
        when(taskService.getAllTasks()).thenReturn(List.of(dto));
        when(taskService.getPaginatedTasks(any(Pageable.class))).thenReturn(List.of(dto));
        when(taskService.getTasksByStatus("TODO")).thenReturn(List.of(dto));
        when(taskService.getSortedTasks(List.of("title"))).thenReturn(List.of(dto));
        when(taskService.getPaginatedAndSortedTasks(0, 5, List.of("id"))).thenReturn(List.of(dto));

        assertThat(taskController.getAllTasks()).containsExactly(dto);
        assertThat(taskController.getPaginatedTasks(2, 3)).containsExactly(dto);
        assertThat(taskController.getTasksByStatus("TODO")).containsExactly(dto);
        assertThat(taskController.getSortedTasks(List.of("title"))).containsExactly(dto);
        assertThat(taskController.getPaginatedAndSortedTasks(0, 5, List.of("id"))).containsExactly(dto);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(taskService).getPaginatedTasks(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(2);
        assertThat(captor.getValue().getPageSize()).isEqualTo(3);
    }

    @Test
    void getUpdateAndDeleteReturnExpectedStatuses() {
        TaskRequestDto request = new TaskRequestDto();
        TaskResponseDto dto = dto(1L, "Task");
        when(taskService.getTaskById(1L)).thenReturn(dto);
        when(taskService.getTaskById(2L)).thenReturn(null);
        when(taskService.updateTask(1L, request)).thenReturn(dto);
        when(taskService.updateTask(2L, request)).thenReturn(null);
        when(taskService.deleteTask(1L)).thenReturn(true);
        when(taskService.deleteTask(2L)).thenReturn(false);

        assertThat(taskController.getTaskById(1L).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskController.getTaskById(2L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(taskController.updateTask(1L, request).getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(taskController.updateTask(2L, request).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(taskController.deleteTask(1L).getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(taskController.deleteTask(2L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createTaskReturnsServiceResponse() {
        TaskRequestDto request = new TaskRequestDto();
        TaskResponseDto response = dto(3L, "Created");
        when(taskService.createTask(request)).thenReturn(response);

        assertThat(taskController.createTask(request)).isSameAs(response);
    }

    private TaskResponseDto dto(Long id, String title) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }
}
