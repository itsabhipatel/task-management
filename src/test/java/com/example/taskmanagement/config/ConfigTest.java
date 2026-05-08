package com.example.taskmanagement.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.repository.AppUserRepository;
import com.example.taskmanagement.repository.CategoryRepository;
import com.example.taskmanagement.repository.EmployeeRepository;
import com.example.taskmanagement.repository.TaskRepository;
import io.swagger.v3.oas.models.OpenAPI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ConfigTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void openApiContainsApiInfo() {
        OpenAPI openAPI = new OpenApiConfig().taskManagementOpenApi();

        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Task Management API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }

    @Test
    void seedDataCreatesDefaultsWhenRepositoriesAreEmpty() throws Exception {
        when(passwordEncoder.encode(org.mockito.ArgumentMatchers.anyString())).thenReturn("encoded");
        when(categoryRepository.findAll()).thenReturn(List.of(category("Dev"), category("Testing"), category("Docs")));
        when(employeeRepository.findAll()).thenReturn(List.of(
                employee("A"), employee("B"), employee("C"), employee("D"), employee("E")));

        CommandLineRunner runner = new DataInitializer()
                .seedData(categoryRepository, employeeRepository, taskRepository, appUserRepository, passwordEncoder);
        runner.run();

        verify(appUserRepository).saveAll(anyList());
        verify(categoryRepository).saveAll(anyList());
        verify(employeeRepository).saveAll(anyList());
        verify(taskRepository).saveAll(anyList());
    }

    @Test
    void seedDataSkipsDefaultsWhenRepositoriesHaveRows() throws Exception {
        when(appUserRepository.count()).thenReturn(1L);
        when(categoryRepository.count()).thenReturn(1L);
        when(employeeRepository.count()).thenReturn(1L);
        when(taskRepository.count()).thenReturn(1L);

        CommandLineRunner runner = new DataInitializer()
                .seedData(categoryRepository, employeeRepository, taskRepository, appUserRepository, passwordEncoder);
        runner.run();

        verify(appUserRepository, never()).saveAll(anyList());
        verify(categoryRepository, never()).saveAll(anyList());
        verify(employeeRepository, never()).saveAll(anyList());
        verify(taskRepository, never()).saveAll(anyList());
    }

    private Category category(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private Employee employee(String name) {
        Employee employee = new Employee();
        employee.setName(name);
        return employee;
    }
}
