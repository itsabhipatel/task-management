package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByStatus(String status);

    List<Task> findByDueDateBeforeAndStatusNot(LocalDateTime dueDate, String status);
}
