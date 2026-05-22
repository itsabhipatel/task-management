package com.example.taskmanagement.specification.filter.operator;

import com.example.taskmanagement.specification.filter.FilterOperatorStrategy;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

@Component
public class LessThanOperator implements FilterOperatorStrategy {

    @Override
    public String getOperator() {
        return "<";
    }

    @Override
    public Predicate apply(
            CriteriaBuilder cb,
            Path<?> path,
            Object value) {

        return cb.lessThan(
                path.as(
                        (Class<?
                                extends Comparable>)
                                path.getJavaType()),
                (Comparable)
                        value);
    }
}


