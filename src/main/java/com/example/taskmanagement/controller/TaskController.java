package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.service.TaskService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public List<TaskResponseDto> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/pagination")
    public Page<TaskResponseDto> getTasksByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return taskService.getTasksByPage(pageable);
    }

    @GetMapping("/status/{status}")
    public List<TaskResponseDto> getTasksByStatus(@PathVariable String status) {
        return taskService.getTasksByStatus(status);
    }

    @GetMapping("/sort")
    public List<TaskResponseDto> getTasksWithSorting(
            @RequestParam String fieldName,
            @RequestParam(defaultValue = "asc") String direction) {

        return taskService.getTasksWithSorting(fieldName, direction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto task = taskService.getTaskById(id);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    @PostMapping
    public TaskResponseDto createTask(@RequestBody TaskRequestDto taskRequestDto) {
        return taskService.createTask(taskRequestDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
                                                      @RequestBody TaskRequestDto taskRequestDto) {
        TaskResponseDto task = taskService.updateTask(id, taskRequestDto);

        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        boolean deleted = taskService.deleteTask(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
