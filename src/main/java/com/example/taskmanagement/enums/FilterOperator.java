package com.example.taskmanagement.enums;

public enum FilterOperator {

    EQUAL("="),
    NOT_EQUAL("!="),
    GREATER_THAN(">"),
    LESS_THAN("<"),
    GREATER_THAN_EQUAL(">="),
    LESS_THAN_EQUAL("<=");

    private final String value;

    FilterOperator(String value) {
        this.value = value;
    }

    public static FilterOperator from(String value) {

        for (FilterOperator operator : values()) {

            if (operator.value.equals(value)) {
                return operator;
            }
        }

        throw new IllegalArgumentException(
                "Invalid operator: " + value);
    }
}