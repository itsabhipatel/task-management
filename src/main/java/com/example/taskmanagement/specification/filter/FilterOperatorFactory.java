package com.example.taskmanagement.specification.filter;

import com.example.taskmanagement.exception.BadRequestRuntimeException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class FilterOperatorFactory {

    private final Map<String, FilterOperatorStrategy> strategyMap;

    public FilterOperatorFactory(List<FilterOperatorStrategy> strategies) {

        this.strategyMap =
                strategies.stream()
                        .collect(
                                Collectors.toMap(
                                        FilterOperatorStrategy
                                                ::getOperator,
                                        Function.identity()
                                ));
    }

    public FilterOperatorStrategy getStrategy(String operator) {

        FilterOperatorStrategy strategy = strategyMap.get(operator.trim());

        if (strategy == null) {

            throw new
                    BadRequestRuntimeException(
                    "Unsupported operator: "
                            + operator);
        }

        return strategy;
    }
}