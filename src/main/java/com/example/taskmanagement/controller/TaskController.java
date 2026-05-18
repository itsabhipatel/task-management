package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskFilterDto;
import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.dto.TaskSummaryDto;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.service.TaskService;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/all")
    public List<TaskResponseDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponseDto> getPaginatedTasks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return taskService.getPaginatedTasks(pageable);
    }

    @GetMapping("/by-status/{status}")
    public List<TaskResponseDto> getTasksByStatus(@PathVariable String status) {
        return taskService.getTasksByStatus(status);
    }

    @GetMapping("/sort")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponseDto> getSortedTasks(
            @RequestParam(defaultValue = "id") List<String> fields) {

        return taskService.getSortedTasks(fields);
    }

    @GetMapping("/page-and-sort")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TaskResponseDto> getPaginatedAndSortedTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") List<String> fields) {

        return taskService.getPaginatedAndSortedTasks(page, size, fields);
    }

    @PostMapping("/filter")
    public List<TaskResponseDto> filterTasks(@RequestBody(required = false) TaskFilterDto filter) {
        return taskService.filterTasks(filter);
    }

    @GetMapping("/summary")
    public TaskSummaryDto getTaskSummary() {
        return taskService.getTaskSummary();
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto task = taskService.getTaskById(id);

        if (task == null) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping("/create")
    public TaskResponseDto createTask(@RequestBody TaskRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
                                                      @RequestBody TaskRequestDto taskRequestDto) {
        TaskResponseDto task = taskService.updateTask(id, taskRequestDto);

        if (task == null) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<TaskResponseDto> updateTaskProgress(@PathVariable Long id,
                                                              @RequestParam Integer progressPercentage) {
        TaskResponseDto task = taskService.updateTaskProgress(id, progressPercentage);

        if (task == null) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);

        if (!deleted) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }

        return ResponseEntity.noContent().build();
    }
}
