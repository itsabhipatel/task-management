package com.example.taskmanagement.specification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.taskmanagement.dto.FilterDto;
import com.example.taskmanagement.exception.BadRequestRuntimeException;
import com.example.taskmanagement.specification.filter.FilterOperatorFactory;
import com.example.taskmanagement.specification.filter.FilterOperatorStrategy;
import com.example.taskmanagement.specification.filter.operator.EqualOperator;
import com.example.taskmanagement.specification.filter.operator.GreaterThanEqualOperator;
import com.example.taskmanagement.specification.filter.operator.GreaterThanOperator;
import com.example.taskmanagement.specification.filter.operator.LessThanEqualOperator;
import com.example.taskmanagement.specification.filter.operator.LessThanOperator;
import com.example.taskmanagement.specification.filter.operator.NotEqualOperator;
import java.util.List;
import org.junit.jupiter.api.Test;

class TaskSpecificationTest {

    private final TaskSpecification taskSpecification = new TaskSpecification(new FilterOperatorFactory(List.of(
            new EqualOperator(),
            new NotEqualOperator(),
            new GreaterThanOperator(),
            new LessThanOperator(),
            new GreaterThanEqualOperator(),
            new LessThanEqualOperator())));

    @Test
    void shouldCreateSpecificationForValidFilters() {
        var specification = taskSpecification.filterTasks(
                List.of(filter("status", "=", "TODO"), filter("progressPercentage", ">=", "50")),
                new java.util.ArrayList<>());

        assertNotNull(specification);
    }

    @Test
    void shouldRejectBlankFilterParts() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("", "", "")), new java.util.ArrayList<>()));

        assertEquals(3, exception.getErrors().size());
    }

    @Test
    void shouldRejectInvalidField() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("unknown", "=", "TODO")), new java.util.ArrayList<>()));

        assertEquals("Invalid filter field: unknown", exception.getErrors().get(0));
    }

    @Test
    void shouldRejectInvalidOperator() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("status", "contains", "TODO")), new java.util.ArrayList<>()));

        assertEquals("Invalid operator: contains", exception.getErrors().get(0));
    }

    @Test
    void shouldRejectUnsupportedOperatorForStringField() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("title", ">", "Task")), new java.util.ArrayList<>()));

        assertEquals("Operator > is not supported for String field: title", exception.getErrors().get(0));
    }

    @Test
    void shouldRejectInvalidNumberValue() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("id", "=", "abc")), new java.util.ArrayList<>()));

        assertEquals("Invalid value: abc", exception.getErrors().get(0));
    }

    @Test
    void shouldRejectInvalidDateValue() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("dueDate", "=", "bad-date")), new java.util.ArrayList<>()));

        assertEquals("Invalid date value: bad-date. Expected format: yyyy-MM-ddTHH:mm:ss", exception.getErrors().get(0));
    }

    @Test
    void shouldRejectInvalidProgressValue() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("progressPercentage", "=", "101")), new java.util.ArrayList<>()));

        assertEquals("Progress percentage must be between 0 and 100", exception.getErrors().get(0));
    }

    @Test
    void shouldRejectInvalidStatusValue() {
        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> taskSpecification.filterTasks(List.of(filter("status", "=", "BLOCKED")), new java.util.ArrayList<>()));

        assertEquals("Invalid status value: BLOCKED. Supported values: [TODO, DONE]", exception.getErrors().get(0));
    }

    @Test
    void shouldReturnRegisteredStrategy() {
        FilterOperatorFactory factory = new FilterOperatorFactory(List.<FilterOperatorStrategy>of(new EqualOperator()));

        assertEquals("=", factory.getStrategy("=").getOperator());
    }

    @Test
    void shouldRejectUnsupportedStrategy() {
        FilterOperatorFactory factory = new FilterOperatorFactory(List.<FilterOperatorStrategy>of(new EqualOperator()));

        BadRequestRuntimeException exception = assertThrows(
                BadRequestRuntimeException.class,
                () -> factory.getStrategy("~"));

        assertEquals("Unsupported operator: ~", exception.getMessage());
    }

    private FilterDto filter(String field, String operator, Object value) {
        FilterDto filter = new FilterDto();
        filter.setField(field);
        filter.setOperator(operator);
        filter.setValue(value);
        return filter;
    }
}
