package com.example.taskmanagement.dto;

import java.time.LocalDateTime;

public class NotificationResponseDto {

    private String status;
    private String message;
    private LocalDateTime sentAt;

    public NotificationResponseDto() {
    }

    public NotificationResponseDto(String status, String message, LocalDateTime sentAt) {
        this.status = status;
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
