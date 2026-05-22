package com.example.taskmanagement.specification.filter.operator;

import com.example.taskmanagement.specification.filter.FilterOperatorStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotEqualOperator implements FilterOperatorStrategy {

    @Override
    public String getOperator() {
        return "!=";
    }

    @Override
    public Predicate apply(CriteriaBuilder cb, Path<?> path, Object value) {

        if (path.getJavaType()
                .equals(String.class)) {

            return cb.notLike(
                    cb.lower(
                            path.as(
                                    String.class)),
                    "%"
                            + value
                            .toString()
                            .toLowerCase()
                            + "%");
        }

        else if (path.getJavaType()
                        .equals(
                                LocalDateTime .class)) {


                   return cb.notEqual(
                            path.as(
                                    LocalDateTime.class),
                            (LocalDateTime)
                                    value);

        }

        return cb.notEqual(
                path,
                value);
    }
}


