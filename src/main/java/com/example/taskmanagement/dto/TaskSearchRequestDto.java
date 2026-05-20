package com.example.taskmanagement.dto;

import java.util.List;

public class TaskSearchRequestDto {

    private List<FilterDto> filters;

    private String sortBy;
    private String sortDirection;

    private Integer page;
    private Integer size;

    public List<FilterDto> getFilters() {
        return filters;
    }

    public void setFilters(List<FilterDto> filters) {
        this.filters = filters;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}