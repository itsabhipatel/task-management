package com.example.taskmanagement.service;

import com.example.taskmanagement.constant.TaskConstants;
import com.example.taskmanagement.dto.TaskRequestDto;
import com.example.taskmanagement.dto.TaskResponseDto;
import com.example.taskmanagement.dto.TaskFilterDto;
import com.example.taskmanagement.dto.TaskSummaryDto;
import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.CategoryRepository;
import com.example.taskmanagement.repository.EmployeeRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.specification.TaskSpecification;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
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

    public List<TaskResponseDto> filterTasks(TaskFilterDto filter) {
        if (filter == null) {
            return convertToResponseDtoList(taskRepository.findAll());
        }

        return convertToResponseDtoList(taskRepository.findAll(TaskSpecification.filterTasks(
                filter.getId(),
                filter.getTitle(),
                filter.getDescription(),
                filter.getStatus(),
                filter.getPriority(),
                filter.getDueDate(),
                filter.getCreatedDate(),
                filter.getUpdatedDate(),
                filter.getCompletedDate(),
                filter.getProgressPercentage(),
                filter.getEmployeeId(),
                filter.getCategoryId()
        )));
    }

    public TaskSummaryDto getTaskSummary() {
        List<Task> tasks = taskRepository.findAll();
        TaskSummaryDto summary = new TaskSummaryDto();
        summary.setTotalTasks(tasks.size());
        summary.setOverdueTasks(tasks.stream().filter(this::isOverdue).count());
        summary.setAverageProgress(calculateAverageProgress(tasks));
        summary.setStatusCounts(countByValue(tasks, true));
        summary.setPriorityCounts(countByValue(tasks, false));
        return summary;
    }

    public TaskResponseDto createTask(TaskRequestDto taskRequestDto) {
        Task task = new Task();
        applyTaskDetails(task, taskRequestDto);
        setEmployeeAndCategory(task, taskRequestDto);

        Task savedTask = taskRepository.save(task);
        return convertToResponseDto(savedTask);
    }

    public TaskResponseDto updateTask(Long id, TaskRequestDto taskRequestDto) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return null;
        }

        applyTaskDetails(task, taskRequestDto);
        setEmployeeAndCategory(task, taskRequestDto);

        Task savedTask = taskRepository.save(task);
        return convertToResponseDto(savedTask);
    }

    public TaskResponseDto updateTaskProgress(Long id, Integer progressPercentage) {
        Task task = taskRepository.findById(id).orElse(null);

        if (task == null) {
            return null;
        }

        int normalizedProgress = normalizeProgress(progressPercentage);
        task.setProgressPercentage(normalizedProgress);
        if (normalizedProgress == 100) {
            task.setStatus(TaskConstants.STATUS_DONE);
            task.setCompletedDate(LocalDateTime.now());
        } else if (TaskConstants.STATUS_DONE.equals(task.getStatus())) {
            task.setStatus(TaskConstants.STATUS_IN_PROGRESS);
            task.setCompletedDate(null);
        }

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

    private void applyTaskDetails(Task task, TaskRequestDto taskRequestDto) {
        task.setTitle(taskRequestDto.getTitle());
        task.setDescription(taskRequestDto.getDescription());
        task.setStatus(defaultText(taskRequestDto.getStatus(), TaskConstants.STATUS_TODO));
        task.setPriority(defaultText(taskRequestDto.getPriority(), TaskConstants.PRIORITY_MEDIUM));
        task.setDueDate(taskRequestDto.getDueDate());
        task.setProgressPercentage(normalizeProgress(taskRequestDto.getProgressPercentage()));

        if (TaskConstants.STATUS_DONE.equals(task.getStatus())
                || task.getProgressPercentage() == TaskConstants.MAX_PROGRESS) {
            task.setStatus(TaskConstants.STATUS_DONE);
            task.setProgressPercentage(TaskConstants.MAX_PROGRESS);
            if (task.getCompletedDate() == null) {
                task.setCompletedDate(LocalDateTime.now());
            }
        } else {
            task.setCompletedDate(null);
        }
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
        taskResponseDto.setDescription(task.getDescription());
        taskResponseDto.setStatus(task.getStatus());
        taskResponseDto.setPriority(task.getPriority());
        taskResponseDto.setDueDate(task.getDueDate());
        taskResponseDto.setCreatedDate(task.getCreatedDate());
        taskResponseDto.setUpdatedDate(task.getUpdatedDate());
        taskResponseDto.setCompletedDate(task.getCompletedDate());
        taskResponseDto.setProgressPercentage(task.getProgressPercentage());
        taskResponseDto.setOverdue(isOverdue(task));

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

    private boolean isOverdue(Task task) {
        return task.getDueDate() != null
                && !TaskConstants.STATUS_DONE.equals(task.getStatus())
                && task.getDueDate().isBefore(LocalDateTime.now());
    }

    private double calculateAverageProgress(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return 0;
        }

        return tasks.stream()
                .map(Task::getProgressPercentage)
                .mapToInt(progress -> progress == null ? 0 : progress)
                .average()
                .orElse(0);
    }

    private Map<String, Long> countByValue(List<Task> tasks, boolean status) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Task task : tasks) {
            String value = status ? task.getStatus() : task.getPriority();
            value = defaultText(value, TaskConstants.UNSET);
            counts.put(value, counts.getOrDefault(value, 0L) + 1);
        }
        return counts;
    }

    private String defaultText(String value, String defaultValue) {
        String normalized = normalizeText(value);
        return normalized == null ? defaultValue : normalized;
    }

    private String normalizeText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private int normalizeProgress(Integer progressPercentage) {
        if (progressPercentage == null) {
            return TaskConstants.MIN_PROGRESS;
        }
        return Math.max(TaskConstants.MIN_PROGRESS, Math.min(TaskConstants.MAX_PROGRESS, progressPercentage));
    }
}
