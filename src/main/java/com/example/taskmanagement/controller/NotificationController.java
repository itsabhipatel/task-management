package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.NotificationRequestDto;
import com.example.taskmanagement.dto.NotificationResponseDto;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @PostMapping
    public NotificationResponseDto sendNotification(@RequestBody NotificationRequestDto notificationRequestDto) {
        return new NotificationResponseDto(
                "SENT",
                "Notification sent for task: " + notificationRequestDto.getTitle(),
                LocalDateTime.now());
    }
}
