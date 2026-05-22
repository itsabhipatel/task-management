package com.example.taskmanagement.specification;

import com.example.taskmanagement.dto.FilterDto;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.enums.FilterOperator;
import com.example.taskmanagement.exception.BadRequestRuntimeException;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TaskSpecification {

    public static Specification<Task> filterTasks(
            List<FilterDto> filters,
            List<String> errors) {

        // Validate filters first
        if (filters != null
                && !filters.isEmpty()) {

            for (FilterDto filter
                    : filters) {

                if (shouldIgnoreFilter(
                        filter)) {

                    continue;
                }
                try {

                    validateFilter(
                            filter);

                } catch (Exception ex) {

                    errors.add(
                            ex.getMessage());
                }
            }
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

                    if (shouldIgnoreFilter(
                            filter)) {

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

                    FilterOperator operator =
                            FilterOperator.from(
                                    filter.getOperator());

                    Predicate predicate;

                    switch (operator) {

                        case EQUAL:

                            if (path.getJavaType()
                                    .equals(
                                            String.class)) {

                                predicate =
                                        criteriaBuilder.like(
                                                criteriaBuilder.lower(
                                                        path.as(
                                                                String.class)),
                                                "%"
                                                        + value
                                                        .toString()
                                                        .toLowerCase()
                                                        + "%");

                            } else if (
                                    path.getJavaType()
                                            .equals(
                                                    LocalDateTime.class)) {

                                predicate =
                                        criteriaBuilder.equal(
                                                path.as(
                                                        LocalDateTime.class),
                                                (LocalDateTime)
                                                        value);

                            } else {

                                predicate =
                                        criteriaBuilder.equal(
                                                path,
                                                value);
                            }

                            break;

                        case NOT_EQUAL:

                            if (path.getJavaType()
                                    .equals(
                                            String.class)) {

                                predicate =
                                        criteriaBuilder.notLike(
                                                criteriaBuilder.lower(
                                                        path.as(
                                                                String.class)),
                                                "%"
                                                        + value
                                                        .toString()
                                                        .toLowerCase()
                                                        + "%");

                            } else if (
                                    path.getJavaType()
                                            .equals(
                                                    LocalDateTime.class)) {

                                predicate =
                                        criteriaBuilder.notEqual(
                                                path.as(
                                                        LocalDateTime.class),
                                                (LocalDateTime)
                                                        value);

                            } else {

                                predicate =
                                        criteriaBuilder.notEqual(
                                                path,
                                                value);
                            }

                            break;

                        case GREATER_THAN:

                            predicate =
                                    criteriaBuilder.greaterThan(
                                            path.as(
                                                    (Class<? extends Comparable>)
                                                            path.getJavaType()),
                                            (Comparable)
                                                    value);

                            break;

                        case LESS_THAN:

                            predicate =
                                    criteriaBuilder.lessThan(
                                            path.as(
                                                    (Class<? extends Comparable>)
                                                            path.getJavaType()),
                                            (Comparable)
                                                    value);

                            break;

                        case GREATER_THAN_EQUAL:

                            predicate =
                                    criteriaBuilder
                                            .greaterThanOrEqualTo(
                                                    path.as(
                                                            (Class<? extends Comparable>)
                                                                    path.getJavaType()),
                                                    (Comparable)
                                                            value);

                            break;

                        case LESS_THAN_EQUAL:

                            predicate =
                                    criteriaBuilder
                                            .lessThanOrEqualTo(
                                                    path.as(
                                                            (Class<? extends Comparable>)
                                                                    path.getJavaType()),
                                                    (Comparable)
                                                            value);

                            break;

                        default:

                            throw new BadRequestRuntimeException(
                                    "Unsupported operator");
                    }

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
            FilterDto filter) {

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

            throw new BadRequestRuntimeException(
                    "Invalid filter field: "
                            + filter.getField());
        }

        // Validate operator
        FilterOperator operator =
                FilterOperator.from(
                        filter.getOperator());

        Class<?> targetType =
                getFieldType(
                        filter.getField());

        // Validate String operators
        if (targetType.equals(
                String.class)
                && operator != FilterOperator.EQUAL
                && operator
                != FilterOperator.NOT_EQUAL) {

            throw new BadRequestRuntimeException(
                    "Operator "
                            + filter.getOperator()
                            + " is not supported "
                            + "for String field: "
                            + filter.getField());
        }

        // Validate value
        convertValue(
                filter.getField(),
                targetType,
                filter.getValue());
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
            FilterDto filter) {

        if (filter == null) {
            return true;
        }

        Object value =
                filter.getValue();

        // Ignore null
        if (value == null) {
            return true;
        }

        // Convert everything to string
        String stringValue =
                value.toString();

        // Ignore "", "   "
        return stringValue
                .trim()
                .isEmpty();
    }
}
