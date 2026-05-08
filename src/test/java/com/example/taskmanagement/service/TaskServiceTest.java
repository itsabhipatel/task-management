package com.example.taskmanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
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
    void getAllTasksMapsEntityDetails() {
        Task task = task(1L, "Build API", "TODO", employee(2L, "Abhi"), category(3L, "Dev"));
        when(taskRepository.findAll()).thenReturn(List.of(task));

        List<TaskResponseDto> result = taskService.getAllTasks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getEmployeeName()).isEqualTo("Abhi");
        assertThat(result.get(0).getCategoryName()).isEqualTo("Dev");
    }

    @Test
    void getTaskByIdReturnsNullWhenMissing() {
        when(taskRepository.findById(9L)).thenReturn(Optional.empty());

        assertThat(taskService.getTaskById(9L)).isNull();
    }

    @Test
    void getPaginatedAndSortedTasksUsesPageable() {
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(task(1L, "A", "DONE", null, null))));

        List<TaskResponseDto> result = taskService.getPaginatedAndSortedTasks(1, 2, List.of("title"));

        assertThat(result).hasSize(1);
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(taskRepository).findAll(captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(1);
        assertThat(captor.getValue().getPageSize()).isEqualTo(2);
        assertThat(captor.getValue().getSort()).isEqualTo(Sort.by("title"));
    }

    @Test
    void getTasksByStatusAndSortedTasksMapLists() {
        when(taskRepository.findByStatus("TODO")).thenReturn(List.of(task(1L, "A", "TODO", null, null)));
        when(taskRepository.findAll(Sort.by("status", "title"))).thenReturn(List.of(task(2L, "B", "DONE", null, null)));

        assertThat(taskService.getTasksByStatus("TODO")).extracting(TaskResponseDto::getStatus).containsExactly("TODO");
        assertThat(taskService.getSortedTasks(List.of("status", "title"))).extracting(TaskResponseDto::getTitle).containsExactly("B");
    }

    @Test
    void createTaskLoadsEmployeeAndCategoryBeforeSaving() {
        TaskRequestDto request = request("New task", "TODO", 5L, 6L);
        Employee employee = employee(5L, "Neha");
        Category category = category(6L, "Testing");
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(employee));
        when(categoryRepository.findById(6L)).thenReturn(Optional.of(category));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        TaskResponseDto result = taskService.createTask(request);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getEmployeeId()).isEqualTo(5L);
        assertThat(result.getCategoryId()).isEqualTo(6L);
    }

    @Test
    void updateTaskReturnsNullWhenMissingAndUpdatesWhenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThat(taskService.updateTask(1L, request("Missing", "TODO", null, null))).isNull();

        Task existing = task(2L, "Old", "TODO", null, null);
        when(taskRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(existing)).thenReturn(existing);

        TaskResponseDto result = taskService.updateTask(2L, request("Updated", "DONE", null, null));

        assertThat(result.getTitle()).isEqualTo("Updated");
        assertThat(result.getStatus()).isEqualTo("DONE");
    }

    @Test
    void deleteTaskReturnsFalseWhenMissingAndDeletesWhenFound() {
        Task task = task(4L, "Delete", "TODO", null, null);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        when(taskRepository.findById(4L)).thenReturn(Optional.of(task));

        assertThat(taskService.deleteTask(1L)).isFalse();
        assertThat(taskService.deleteTask(4L)).isTrue();
        verify(taskRepository).delete(task);
    }

    private TaskRequestDto request(String title, String status, Long employeeId, Long categoryId) {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle(title);
        request.setStatus(status);
        request.setEmployeeId(employeeId);
        request.setCategoryId(categoryId);
        return request;
    }

    private Task task(Long id, String title, String status, Employee employee, Category category) {
        Task task = new Task();
        task.setId(id);
        task.setTitle(title);
        task.setStatus(status);
        task.setCreatedDate(LocalDateTime.of(2026, 5, 9, 10, 0));
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
