package com.example.taskmanagement.config;

import com.example.taskmanagement.entity.AppUser;
import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.AppUserRepository;
import com.example.taskmanagement.repository.CategoryRepository;
import com.example.taskmanagement.repository.EmployeeRepository;
import com.example.taskmanagement.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedData(CategoryRepository categoryRepository,
                                      EmployeeRepository employeeRepository,
                                      TaskRepository taskRepository,
                                      AppUserRepository appUserRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (appUserRepository.count() == 0) {
                appUserRepository.saveAll(List.of(
                        createUser("admin", "admin123", "ADMIN", passwordEncoder),
                        createUser("user", "user123", "USER", passwordEncoder)
                ));
            }

            if (categoryRepository.count() == 0) {
                categoryRepository.saveAll(List.of(
                        createCategory("Development"),
                        createCategory("Testing"),
                        createCategory("Documentation")
                ));
            }

            if (employeeRepository.count() == 0) {
                employeeRepository.saveAll(List.of(
                        createEmployee("Abhi Patel"),
                        createEmployee("Neha Sharma"),
                        createEmployee("Rahul Mehta"),
                        createEmployee("Priya Nair"),
                        createEmployee("Amit Shah")
                ));
            }

            if (taskRepository.count() == 0) {
                List<Category> categories = categoryRepository.findAll();
                List<Employee> employees = employeeRepository.findAll();

                taskRepository.saveAll(List.of(
                        createTask("Build login API", "IN_PROGRESS", employees.get(0), categories.get(0), 2),
                        createTask("Write JWT filter tests", "TODO", employees.get(1), categories.get(1), 1),
                        createTask("Prepare API documentation", "DONE", employees.get(2), categories.get(2), 4),
                        createTask("Review task sorting endpoint", "TODO", employees.get(0), categories.get(0), 0),
                        createTask("Create task pagination API", "DONE", employees.get(3), categories.get(0), 5),
                        createTask("Test H2 database setup", "IN_PROGRESS", employees.get(4), categories.get(1), 3),
                        createTask("Update Swagger security docs", "TODO", employees.get(2), categories.get(2), 6),
                        createTask("Fix task update validation", "TODO", employees.get(1), categories.get(0), 7),
                        createTask("Check authorization errors", "DONE", employees.get(3), categories.get(1), 8),
                        createTask("Clean demo test cases", "IN_PROGRESS", employees.get(4), categories.get(2), 9)
                ));
            }
        };
    }

    private AppUser createUser(String userId, String password, String role, PasswordEncoder passwordEncoder) {
        AppUser appUser = new AppUser();
        appUser.setUserId(userId);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRole(role);
        return appUser;
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private Employee createEmployee(String name) {
        Employee employee = new Employee();
        employee.setName(name);
        return employee;
    }

    private Task createTask(String title,
                            String status,
                            Employee employee,
                            Category category,
                            int createdDaysAgo) {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        task.setEmployee(employee);
        task.setCategory(category);
        task.setCreatedDate(LocalDateTime.now().minusDays(createdDaysAgo));
        return task;
    }
}
