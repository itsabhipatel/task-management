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
            List<FilterDto> filters) {

        return (root,
                query,
                criteriaBuilder) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            try {

                for (FilterDto filter
                        : filters) {

                    Path<?> path =
                            getPath(
                                    root,
                                    filter.getField());

                    Object value =
                            convertValue(
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

                            if (path.getJavaType()
                                    .equals(
                                            String.class)) {

                                throw new BadRequestRuntimeException(
                                        "Operator > "
                                                + "is not supported "
                                                + "for String field: "
                                                + filter.getField());
                            }

                            if (path.getJavaType()
                                    .equals(
                                            LocalDateTime.class)) {

                                predicate =
                                        criteriaBuilder.greaterThan(
                                                path.as(
                                                        LocalDateTime.class),
                                                (LocalDateTime)
                                                        value);

                            } else {

                                predicate =
                                        criteriaBuilder.greaterThan(
                                                path.as(
                                                        (Class<? extends Comparable>)
                                                                path.getJavaType()),
                                                (Comparable)
                                                        value);
                            }

                            break;

                        case LESS_THAN:

                            if (path.getJavaType()
                                    .equals(
                                            String.class)) {

                                throw new BadRequestRuntimeException(
                                        "Operator < "
                                                + "is not supported "
                                                + "for String field: "
                                                + filter.getField());
                            }

                            if (path.getJavaType()
                                    .equals(
                                            LocalDateTime.class)) {

                                predicate =
                                        criteriaBuilder.lessThan(
                                                path.as(
                                                        LocalDateTime.class),
                                                (LocalDateTime)
                                                        value);

                            } else {

                                predicate =
                                        criteriaBuilder.lessThan(
                                                path.as(
                                                        (Class<? extends Comparable>)
                                                                path.getJavaType()),
                                                (Comparable)
                                                        value);
                            }

                            break;

                        case GREATER_THAN_EQUAL:

                            if (path.getJavaType()
                                    .equals(
                                            String.class)) {

                                throw new BadRequestRuntimeException(
                                        "Operator >= "
                                                + "is not supported "
                                                + "for String field: "
                                                + filter.getField());
                            }

                            if (path.getJavaType()
                                    .equals(
                                            LocalDateTime.class)) {

                                predicate =
                                        criteriaBuilder
                                                .greaterThanOrEqualTo(
                                                        path.as(
                                                                LocalDateTime.class),
                                                        (LocalDateTime)
                                                                value);

                            } else {

                                predicate =
                                        criteriaBuilder
                                                .greaterThanOrEqualTo(
                                                        path.as(
                                                                (Class<? extends Comparable>)
                                                                        path.getJavaType()),
                                                        (Comparable)
                                                                value);
                            }

                            break;

                        case LESS_THAN_EQUAL:

                            if (path.getJavaType()
                                    .equals(
                                            String.class)) {

                                throw new BadRequestRuntimeException(
                                        "Operator <= "
                                                + "is not supported "
                                                + "for String field: "
                                                + filter.getField());
                            }

                            if (path.getJavaType()
                                    .equals(
                                            LocalDateTime.class)) {

                                predicate =
                                        criteriaBuilder
                                                .lessThanOrEqualTo(
                                                        path.as(
                                                                LocalDateTime.class),
                                                        (LocalDateTime)
                                                                value);

                            } else {

                                predicate =
                                        criteriaBuilder
                                                .lessThanOrEqualTo(
                                                        path.as(
                                                                (Class<? extends Comparable>)
                                                                        path.getJavaType()),
                                                        (Comparable)
                                                                value);
                            }

                            break;

                        default:

                            throw new BadRequestRuntimeException(
                                    "Unsupported operator");
                    }

                    predicates.add(
                            predicate);
                }

                return criteriaBuilder.and(
                        predicates.toArray(
                                new Predicate[0]));

            } catch (
                    BadRequestRuntimeException ex) {

                throw ex;

            } catch (
                    Exception ex) {

                throw new BadRequestRuntimeException(
                        ex.getMessage());
            }
        };
    }

    private static Path<?> getPath(
            Root<Task> root,
            String field) {

        try {

            if (field.contains(".")) {

                String[] parts =
                        field.split("\\.");

                return root.get(parts[0])
                        .get(parts[1]);
            }

            return root.get(field);

        } catch (
                IllegalArgumentException ex) {

            throw new BadRequestRuntimeException(
                    "Invalid filter field: "
                            + field);
        }
    }

    private static Object convertValue(
            Class<?> targetType,
            Object value) {

        try {

            if (targetType.equals(
                    Long.class)) {

                return Long.valueOf(
                        value.toString());
            }

            if (targetType.equals(
                    Integer.class)) {

                Integer convertedValue =
                        Integer.valueOf(
                                value.toString());

                // Validate percentage
                if (convertedValue < 0
                        || convertedValue > 100) {

                    throw new BadRequestRuntimeException(
                            "Progress percentage "
                                    + "must be between "
                                    + "0 and 100");
                }

                return convertedValue;
            }

            if (targetType.equals(
                    LocalDateTime.class)) {

                return LocalDateTime.parse(
                        value.toString());
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
}