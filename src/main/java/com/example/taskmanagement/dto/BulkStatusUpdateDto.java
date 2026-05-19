package com.example.taskmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class BulkStatusUpdateDto {

    @NotNull(message = "Task IDs cannot be null")
    @NotEmpty(message = "Task IDs cannot be empty")
    private List<Long> taskIds;
    @NotBlank(message = "Status cannot be blank")
    private String status;

    public List<Long> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<Long> taskIds) {
        this.taskIds = taskIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
