package com.example.taskmanagement.dto;

public class NotificationRequestDto {

    private Long taskId;
    private String title;
    private String message;

    public NotificationRequestDto() {
    }

    public NotificationRequestDto(Long taskId, String title, String message) {
        this.taskId = taskId;
        this.title = title;
        this.message = message;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
