package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByStatus(String status, Pageable pageable);

    List<Task> findByStatus(String status);
}
