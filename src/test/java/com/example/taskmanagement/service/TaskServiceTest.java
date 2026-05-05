package com.example.taskmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
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

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, employeeRepository, categoryRepository);
    }

    @Test
    void getAllTasksReturnsTaskDtos() {
        when(taskRepository.findAll()).thenReturn(List.of(task()));

        List<TaskResponseDto> response = taskService.getAllTasks();

        assertEquals(1, response.size());
        assertEquals("Build API", response.get(0).getTitle());
        assertEquals("Abhi Patel", response.get(0).getEmployeeName());
        assertEquals("Development", response.get(0).getCategoryName());
    }

    @Test
    void getTaskByIdReturnsTaskWhenFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task()));

        TaskResponseDto response = taskService.getTaskById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getTaskByIdReturnsNullWhenMissing() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskResponseDto response = taskService.getTaskById(1L);

        assertNull(response);
    }

    @Test
    void getPaginatedTasksReturnsPage() {
        Pageable pageable = PageRequest.of(0, 5);
        when(taskRepository.findAll(pageable)).thenReturn(new PageImpl<Task>(List.of(task()), pageable, 1));

        Page<TaskResponseDto> response = taskService.getPaginatedTasks(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Build API", response.getContent().get(0).getTitle());
    }

    @Test
    void getTasksByStatusReturnsMatchingTasks() {
        when(taskRepository.findByStatus("TODO")).thenReturn(List.of(task()));

        List<TaskResponseDto> response = taskService.getTasksByStatus("TODO");

        assertEquals(1, response.size());
        assertEquals("TODO", response.get(0).getStatus());
    }

    @Test
    void getSortedTasksUsesSort() {
        when(taskRepository.findAll(any(Sort.class))).thenReturn(List.of(task()));

        List<TaskResponseDto> response = taskService.getSortedTasks(List.of("title"));

        assertEquals(1, response.size());
        verify(taskRepository).findAll(any(Sort.class));
    }

    @Test
    void getPaginatedAndSortedTasksUsesPageAndSort() {
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<Task>(List.of(task())));

        Page<TaskResponseDto> response =
                taskService.getPaginatedAndSortedTasks(0, 5, List.of("title"));

        assertEquals(1, response.getTotalElements());
        verify(taskRepository).findAll(any(Pageable.class));
    }

    @Test
    void createTaskSavesTask() {
        Employee employee = employee();
        Category category = category();
        Task savedTask = task();
        TaskRequestDto request = taskRequest();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponseDto response = taskService.createTask(request);

        assertEquals("Build API", response.getTitle());
        assertEquals(1L, response.getEmployeeId());
        assertEquals(1L, response.getCategoryId());
    }

    @Test
    void updateTaskUpdatesExistingTask() {
        Task existingTask = task();
        TaskRequestDto request = taskRequest();
        request.setTitle("Updated API");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category()));
        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        TaskResponseDto response = taskService.updateTask(1L, request);

        assertEquals("Updated API", response.getTitle());
    }

    @Test
    void deleteTaskReturnsTrueWhenFound() {
        Task task = task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        boolean deleted = taskService.deleteTask(1L);

        assertTrue(deleted);
        verify(taskRepository).delete(task);
    }

    @Test
    void deleteTaskReturnsFalseWhenMissing() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        boolean deleted = taskService.deleteTask(1L);

        assertFalse(deleted);
    }

    @Test
    void getSortedTasksAcceptsMultipleFields() {
        ArgumentCaptor<Sort> captor = ArgumentCaptor.forClass(Sort.class);
        when(taskRepository.findAll(any(Sort.class))).thenReturn(List.of(task()));

        taskService.getSortedTasks(List.of("status", "title"));

        verify(taskRepository).findAll(captor.capture());
        assertEquals(Sort.Direction.ASC, captor.getValue().getOrderFor("status").getDirection());
        assertEquals(Sort.Direction.ASC, captor.getValue().getOrderFor("title").getDirection());
    }

    private TaskRequestDto taskRequest() {
        TaskRequestDto request = new TaskRequestDto();
        request.setTitle("Build API");
        request.setStatus("TODO");
        request.setEmployeeId(1L);
        request.setCategoryId(1L);
        return request;
    }

    private Task task() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Build API");
        task.setStatus("TODO");
        task.setEmployee(employee());
        task.setCategory(category());
        return task;
    }

    private Employee employee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Abhi Patel");
        return employee;
    }

    private Category category() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Development");
        return category;
    }
}
