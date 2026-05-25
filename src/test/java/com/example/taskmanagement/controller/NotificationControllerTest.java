package com.example.taskmanagement.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.taskmanagement.dto.NotificationRequestDto;
import com.example.taskmanagement.dto.NotificationResponseDto;
import org.junit.jupiter.api.Test;

class NotificationControllerTest {

    private final NotificationController notificationController = new NotificationController();

    @Test
    void shouldSendNotification() {
        NotificationRequestDto request = new NotificationRequestDto(1L, "Task", "Created");

        NotificationResponseDto result = notificationController.sendNotification(request);

        assertEquals("SENT", result.getStatus());
        assertEquals("Notification sent for task: Task", result.getMessage());
        assertNotNull(result.getSentAt());
    }
}
