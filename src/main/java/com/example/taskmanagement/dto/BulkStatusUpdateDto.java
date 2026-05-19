package com.example.taskmanagement.dto;

import java.util.List;

public class BulkStatusUpdateDto {

    private List<Long> taskIds;
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
