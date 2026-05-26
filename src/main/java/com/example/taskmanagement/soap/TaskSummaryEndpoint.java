package com.example.taskmanagement.soap;

import com.example.taskmanagement.dto.TaskSummaryDto;
import com.example.taskmanagement.service.TaskService;
import java.util.List;
import java.util.Map;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class TaskSummaryEndpoint {

    public static final String NAMESPACE_URI = "http://example.com/taskmanagement/soap";

    private final TaskService taskService;

    public TaskSummaryEndpoint(TaskService taskService) {
        this.taskService = taskService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetTaskSummaryRequest")
    @ResponsePayload
    public GetTaskSummaryResponse getTaskSummary(@RequestPayload GetTaskSummaryRequest request) {
        TaskSummaryDto summary = taskService.getTaskSummary();

        GetTaskSummaryResponse response = new GetTaskSummaryResponse();
        response.setTotalTasks(summary.getTotalTasks());
        response.setOverdueTasks(summary.getOverdueTasks());
        response.setAverageProgress(summary.getAverageProgress());
        response.setStatusCounts(toSummaryCounts(summary.getStatusCounts()));
        response.setPriorityCounts(toSummaryCounts(summary.getPriorityCounts()));
        return response;
    }

    private List<SummaryCount> toSummaryCounts(Map<String, Long> counts) {
        return counts.entrySet().stream()
                .map(entry -> new SummaryCount(entry.getKey(), entry.getValue()))
                .toList();
    }
}
