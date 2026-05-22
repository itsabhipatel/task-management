package com.example.taskmanagement.specification;

import com.example.taskmanagement.dto.FilterDto;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.enums.FilterOperator;
import com.example.taskmanagement.exception.BadRequestRuntimeException;
import com.example.taskmanagement.specification.filter.FilterOperatorFactory;
import com.example.taskmanagement.specification.filter.FilterOperatorStrategy;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskSpecification {

    private final FilterOperatorFactory operatorFactory;

    public TaskSpecification(
            FilterOperatorFactory operatorFactory) {

        this.operatorFactory =
                operatorFactory;
    }


    public Specification<Task> filterTasks(
            List<FilterDto> filters,
            List<String> errors) {

        // Validate filters first
        if (filters != null
                && !filters.isEmpty()) {

            for (FilterDto filter
                    : filters) {

                // Validate null/blank field, operator, value
                if (shouldIgnoreFilter(
                        filter,
                        errors)) {

                    continue;
                }

                validateFilter(
                        filter,
                        errors);
            }
        }

        // Stop execution if validation failed
        if (!errors.isEmpty()) {

            throw new BadRequestRuntimeException(
                    errors);
        }

        // Return specification
        return (root,
                query,
                criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            if (filters != null
                    && !filters.isEmpty()) {

                for (FilterDto filter
                        : filters) {

                    // Skip invalid filters safely
                    if (filter == null
                            || isNullOrBlank(
                            filter.getField())
                            || isNullOrBlank(
                            filter.getOperator())
                            || isNullOrBlank(
                            filter.getValue())) {

                        continue;
                    }

                    Path<?> path =
                            getPath(
                                    root,
                                    filter.getField());

                    Object value =
                            convertValue(
                                    filter.getField(),
                                    path.getJavaType(),
                                    filter.getValue());


                   FilterOperatorStrategy filterOperatorStrategy = operatorFactory
                           .getStrategy(filter.getOperator());

                    Predicate predicate = filterOperatorStrategy.apply(
                            criteriaBuilder,
                            path,
                            value);



                    predicates.add(
                            predicate);
                }
            }

            return criteriaBuilder.and(
                    predicates.toArray(
                            new Predicate[0]));
        };
    }

    private static void validateFilter(
            FilterDto filter,
            List<String> errors) {

        List<String> validFields =
                List.of(
                        "id",
                        "title",
                        "description",
                        "status",
                        "priority",
                        "dueDate",
                        "createdDate",
                        "updatedDate",
                        "completedDate",
                        "progressPercentage",
                        "employee.id",
                        "employee.name",
                        "category.id",
                        "category.name");

        // Validate field
        if (!validFields.contains(
                filter.getField())) {

            errors.add(
                    "Invalid filter field: "
                            + filter.getField());

            return;
        }

        // Validate operator
        FilterOperator operator =
                null;

        try {

            operator =
                    FilterOperator.from(
                            filter.getOperator());

        } catch (Exception ex) {

            errors.add(
                    "Invalid operator: "
                            + filter.getOperator());
        }

        Class<?> targetType =
                getFieldType(
                        filter.getField());

        // Validate String operators
        if (operator != null
                && targetType.equals(
                String.class)
                && operator != FilterOperator.EQUAL
                && operator
                != FilterOperator.NOT_EQUAL) {

            errors.add(
                    "Operator "
                            + filter.getOperator()
                            + " is not supported "
                            + "for String field: "
                            + filter.getField());
        }

        // Validate value
        try {

            convertValue(
                    filter.getField(),
                    targetType,
                    filter.getValue());

        } catch (Exception ex) {

            errors.add(
                    ex.getMessage());
        }
    }

    private static Path<?> getPath(
            Root<Task> root,
            String field) {

        if (field.contains(".")) {

            String[] parts =
                    field.split("\\.");

            Path<?> path =
                    root.get(
                            parts[0]);

            for (int i = 1;
                 i < parts.length;
                 i++) {

                path =
                        path.get(
                                parts[i]);
            }

            return path;
        }

        return root.get(field);
    }

    private static Class<?> getFieldType(
            String field) {

        switch (field) {

            case "id":
            case "employee.id":
            case "category.id":
                return Long.class;

            case "progressPercentage":
                return Integer.class;

            case "dueDate":
            case "createdDate":
            case "updatedDate":
            case "completedDate":
                return LocalDateTime.class;

            default:
                return String.class;
        }
    }

    private static Object convertValue(
            String field,
            Class<?> targetType,
            Object value) {

        try {

            // Long
            if (targetType.equals(
                    Long.class)) {

                return Long.valueOf(
                        value.toString());
            }

            // Integer
            if (targetType.equals(
                    Integer.class)) {

                Integer convertedValue =
                        Integer.valueOf(
                                value.toString());

                // Only for progressPercentage
                if ("progressPercentage".equals(
                        field)
                        && (convertedValue < 0
                        || convertedValue > 100)) {

                    throw new BadRequestRuntimeException(
                            "Progress percentage "
                                    + "must be between "
                                    + "0 and 100");
                }

                return convertedValue;
            }

            // Date
            if (targetType.equals(
                    LocalDateTime.class)) {

                return LocalDateTime.parse(
                        value.toString());
            }

            // Status validation
            if ("status".equals(
                    field)) {

                List<String> validStatus =
                        List.of(
                                "TODO",
                                "DONE");

                String status =
                        value.toString()
                                .toUpperCase();

                if (!validStatus.contains(
                        status)) {

                    throw new BadRequestRuntimeException(
                            "Invalid status value: "
                                    + value
                                    + ". Supported values: "
                                    + validStatus);
                }

                return status;
            }

            return value;

        } catch (
                NumberFormatException ex) {

            throw new BadRequestRuntimeException(
                    "Invalid value: "
                            + value);

        } catch (
                DateTimeParseException ex) {

            throw new BadRequestRuntimeException(
                    "Invalid date value: "
                            + value
                            + ". Expected format: "
                            + "yyyy-MM-ddTHH:mm:ss");
        }
    }

    private static boolean shouldIgnoreFilter(
            FilterDto filter,
            List<String> errors) {

        if (filter == null) {
            return true;
        }

        boolean hasError =
                false;

        // Field
        if (isNullOrBlank(
                filter.getField())) {

            errors.add(
                    "Filter field cannot be null or empty");

            hasError =
                    true;
        }

        // Operator
        if (isNullOrBlank(
                filter.getOperator())) {

            errors.add(
                    "Filter operator cannot be null or empty");

            hasError =
                    true;
        }

        // Value
        if (isNullOrBlank(
                filter.getValue())) {

            errors.add(
                    "Filter value cannot be null or empty");

            hasError =
                    true;
        }

        return hasError;
    }

    private static boolean isNullOrBlank(
            Object value) {

        return value == null
                || value.toString()
                .trim()
                .isEmpty();
    }
}