package com.example.taskmanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

import lombok.*;

@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String description;

    private String status;

    private String priority;

    private LocalDateTime dueDate;

    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    private LocalDateTime completedDate;

    private Integer progressPercentage;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    public void setCreatedDateBeforeSave() {
        if (createdDate == null) {
            createdDate = LocalDateTime.now();
        }
        setUpdatedDateBeforeSave();
    }

    @PreUpdate
    public void setUpdatedDateBeforeSave() {
        updatedDate = LocalDateTime.now();
    }
}
