package com.example.taskmanagement.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.entity.AppUser;
import com.example.taskmanagement.entity.Category;
import com.example.taskmanagement.entity.Employee;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.repository.AppUserRepository;
import com.example.taskmanagement.repository.CategoryRepository;
import com.example.taskmanagement.repository.EmployeeRepository;
import com.example.taskmanagement.repository.TaskRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

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

    private CommandLineRunner runner;

    @BeforeEach
    void setUp() {
        runner = new DataInitializer().seedData(
                categoryRepository,
                employeeRepository,
                taskRepository,
                appUserRepository,
                passwordEncoder);
    }

    @Test
    void seedDataAddsTestDataWhenTablesAreEmpty() throws Exception {
        when(appUserRepository.count()).thenReturn(0L);
        when(categoryRepository.count()).thenReturn(0L);
        when(employeeRepository.count()).thenReturn(0L);
        when(taskRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode("admin123")).thenReturn("encoded-admin");
        when(passwordEncoder.encode("user123")).thenReturn("encoded-user");
        when(categoryRepository.findAll()).thenReturn(List.of(category(), category(), category()));
        when(employeeRepository.findAll()).thenReturn(List.of(
                employee(),
                employee(),
                employee(),
                employee(),
                employee()));

        runner.run();

        ArgumentCaptor<Iterable<AppUser>> usersCaptor = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable<Category>> categoriesCaptor = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable<Employee>> employeesCaptor = ArgumentCaptor.forClass(Iterable.class);
        ArgumentCaptor<Iterable<Task>> tasksCaptor = ArgumentCaptor.forClass(Iterable.class);

        verify(appUserRepository).saveAll(usersCaptor.capture());
        verify(categoryRepository).saveAll(categoriesCaptor.capture());
        verify(employeeRepository).saveAll(employeesCaptor.capture());
        verify(taskRepository).saveAll(tasksCaptor.capture());

        List<AppUser> users = toList(usersCaptor.getValue());
        assertEquals(2, users.size());
        assertEquals("admin", users.get(0).getUserId());
        assertEquals("encoded-admin", users.get(0).getPassword());
        assertEquals("ADMIN", users.get(0).getRole());
        assertEquals(3, toList(categoriesCaptor.getValue()).size());
        assertEquals(5, toList(employeesCaptor.getValue()).size());
        assertEquals(10, toList(tasksCaptor.getValue()).size());
    }

    @Test
    void seedDataSkipsTablesThatAlreadyHaveData() throws Exception {
        when(appUserRepository.count()).thenReturn(1L);
        when(categoryRepository.count()).thenReturn(1L);
        when(employeeRepository.count()).thenReturn(1L);
        when(taskRepository.count()).thenReturn(1L);

        runner.run();

        verify(appUserRepository, never()).saveAll(any());
        verify(categoryRepository, never()).saveAll(any());
        verify(employeeRepository, never()).saveAll(any());
        verify(taskRepository, never()).saveAll(any());
    }

    private Category category() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Development");
        return category;
    }

    private Employee employee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Abhi Patel");
        return employee;
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        List<T> values = new ArrayList<T>();
        for (T value : iterable) {
            values.add(value);
        }
        return values;
    }
}
