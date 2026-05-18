package com.example.taskmanagement.specification;

import com.example.taskmanagement.entity.Task;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    private TaskSpecification() {
    }

    public static Specification<Task> filterTasks(
            Long id,
            String title,
            String description,
            String status,
            String priority,
            LocalDateTime dueDate,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            LocalDateTime completedDate,
            Integer progressPercentage,
            Long employeeId,
            Long categoryId
    ) {

        return (root, query, criteriaBuilder) -> {

            // Store all dynamic conditions
            List<Predicate> predicates = new ArrayList<>();

            // Id filter
            if (id != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("id"),
                                id
                        )
                );
            }

            // Title filter (case-insensitive + partial match)
            if (title != null && !title.isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("title")),
                                "%" + title.toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            // Description filter (case-insensitive + partial match)
            if (description != null && !description.isEmpty()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("description")),
                                "%" + description.toLowerCase(Locale.ROOT) + "%"
                        )
                );
            }

            // Status filter
            if (status != null && !status.isEmpty()) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                status.toUpperCase(Locale.ROOT)
                        )
                );
            }

            // Priority filter
            if (priority != null && !priority.isEmpty()) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("priority"),
                                priority.toUpperCase(Locale.ROOT)
                        )
                );
            }

            // Due date filter
            if (dueDate != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("dueDate"),
                                dueDate
                        )
                );
            }

            // Created date filter
            if (createdDate != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("createdDate"),
                                createdDate
                        )
                );
            }

            // Updated date filter
            if (updatedDate != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("updatedDate"),
                                updatedDate
                        )
                );
            }

            // Completed date filter
            if (completedDate != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("completedDate"),
                                completedDate
                        )
                );
            }

            // Progress percentage filter
            if (progressPercentage != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("progressPercentage"),
                                progressPercentage
                        )
                );
            }

            // Employee filter
            if (employeeId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("employee").get("id"),
                                employeeId
                        )
                );
            }

            // Category filter
            if (categoryId != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("category").get("id"),
                                categoryId
                        )
                );
            }

            // Combine all conditions using AND
            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
