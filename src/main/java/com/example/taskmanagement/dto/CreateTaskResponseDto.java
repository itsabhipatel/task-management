package com.example.taskmanagement.dto;

public class CreateTaskResponseDto {

    private TaskResponseDto task;

    private NotificationResponseDto
            notification;

    public CreateTaskResponseDto(
            TaskResponseDto task,
            NotificationResponseDto
                    notification) {

        this.task = task;
        this.notification =
                notification;
    }

    public TaskResponseDto getTask() {
        return task;
    }

    public NotificationResponseDto
    getNotification() {

        return notification;
    }
}