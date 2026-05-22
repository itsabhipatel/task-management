package com.example.taskmanagement.specification.filter;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public interface FilterOperatorStrategy {

    String getOperator();

    Predicate apply(
            CriteriaBuilder cb,
            Path<?> path,
            Object value
    );
}