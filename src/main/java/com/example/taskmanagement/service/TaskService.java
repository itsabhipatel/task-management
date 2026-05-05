package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.CategoryRepository;
import com.example.taskmanagement.repository.EmployeeRepository;
import com.example.taskmanagement.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;
    private final CategoryRepository categoryRepository;

    public TaskService(TaskRepository taskRepository,
                       EmployeeRepository employeeRepository,
                       CategoryRepository categoryRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<TaskResponseDto> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskResponseDto> taskResponseDtos = new ArrayList<>();

        for (Task task : tasks) {
            taskResponseDtos.add(convertToResponseDto(task));
        }

        return taskResponseDtos;
    }

    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return null;
        }

        return convertToResponseDto(task);
    }

    public List<TaskResponseDto> getPaginatedTasks(Pageable pageable) {
        Page<Task> taskPage = taskRepository.findAll(pageable);
        List<TaskResponseDto> taskResponseDtos = new ArrayList<>();

        for (Task task : taskPage.getContent()) {
            taskResponseDtos.add(convertToResponseDto(task));
        }

        return taskResponseDtos;
    }

    public List<TaskResponseDto> getTasksByStatus(String status) {
        List<Task> tasks = taskRepository.findByStatus(status);
        return convertToResponseDtoList(tasks);
    }

    public List<TaskResponseDto> getSortedTasks(List<String> fields) {
        Sort sort = Sort.by(fields.toArray(new String[0]));
        List<Task> tasks = taskRepository.findAll(sort);
        return convertToResponseDtoList(tasks);
    }

    public List<TaskResponseDto> getPaginatedAndSortedTasks(int page,
                                                            int size,
                                                            List<String> fields) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(fields.toArray(new String[0])));
        return getPaginatedTasks(pageable);
    }

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Task task = new Task();
        task.setTitle(taskRequestDto.getTitle());
        task.setStatus(taskRequestDto.getStatus());
        setEmployeeAndCategory(task, taskRequestDto);

        Task savedTask = taskRepository.save(task);
        return convertToResponseDto(savedTask);
    }

    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return null;
        }

        task.setTitle(taskRequestDto.getTitle());
        task.setStatus(taskRequestDto.getStatus());
        setEmployeeAndCategory(task, taskRequestDto);

        Task savedTask = taskRepository.save(task);
        return convertToResponseDto(savedTask);
    }

    public boolean deleteTask(Long id) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return false;
        }

        taskRepository.delete(task);
        return true;
    }

    private void setEmployeeAndCategory(Task task, TaskRequestDto taskRequestDto) {
        Employee employee = null;
        Category category = null;

        if (taskRequestDto.getEmployeeId() != null) {
            employee = employeeRepository.findById(taskRequestDto.getEmployeeId()).orElse(null);
        }

        if (taskRequestDto.getCategoryId() != null) {
            category = categoryRepository.findById(taskRequestDto.getCategoryId()).orElse(null);
        }

        task.setEmployee(employee);
        task.setCategory(category);
    }

    private List<TaskResponseDto> convertToResponseDtoList(List<Task> tasks) {
        List<TaskResponseDto> taskResponseDtos = new ArrayList<>();

        for (Task task : tasks) {
            taskResponseDtos.add(convertToResponseDto(task));
        }

        return taskResponseDtos;
    }

    private TaskResponseDto convertToResponseDto(Task task) {
        TaskResponseDto taskResponseDto = new TaskResponseDto();

        taskResponseDto.setId(task.getId());
        taskResponseDto.setTitle(task.getTitle());
        taskResponseDto.setStatus(task.getStatus());
        taskResponseDto.setCreatedDate(task.getCreatedDate());

        if (task.getEmployee() != null) {
            taskResponseDto.setEmployeeId(task.getEmployee().getId());
            taskResponseDto.setEmployeeName(task.getEmployee().getName());
        }

        if (task.getCategory() != null) {
            taskResponseDto.setCategoryId(task.getCategory().getId());
            taskResponseDto.setCategoryName(task.getCategory().getName());
        }

        return taskResponseDto;
    }
}
