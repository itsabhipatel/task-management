package com.example.taskmanagement.client;

import com.example.taskmanagement.dto.NotificationRequestDto;
import com.example.taskmanagement.dto.NotificationResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notificationClient", url = "${notification.api.url:http://localhost:${server.port:8080}}")
public interface NotificationClient {

    @PostMapping("/api/notifications")
    NotificationResponseDto sendNotification(@RequestBody NotificationRequestDto notificationRequestDto);
}
