package com.example.taskmanagement.soap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.taskmanagement.dto.TaskSummaryDto;
import com.example.taskmanagement.service.TaskService;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskSummaryEndpointTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskSummaryEndpoint endpoint;

    @Test
    void shouldReturnTaskSummaryAsSoapResponse() {
        TaskSummaryDto summary = new TaskSummaryDto();
        summary.setTotalTasks(10);
        summary.setOverdueTasks(2);
        summary.setAverageProgress(75.5);
        summary.setStatusCounts(counts("TODO", 4));
        summary.setPriorityCounts(counts("HIGH", 3));
        when(taskService.getTaskSummary()).thenReturn(summary);

        GetTaskSummaryResponse response = endpoint.getTaskSummary(new GetTaskSummaryRequest());

        assertEquals(10, response.getTotalTasks());
        assertEquals(2, response.getOverdueTasks());
        assertEquals(75.5, response.getAverageProgress());
        assertEquals("TODO", response.getStatusCounts().get(0).getName());
        assertEquals(4, response.getStatusCounts().get(0).getCount());
        assertEquals("HIGH", response.getPriorityCounts().get(0).getName());
        assertEquals(3, response.getPriorityCounts().get(0).getCount());
    }

    private Map<String, Long> counts(String name, long count) {
        Map<String, Long> counts = new LinkedHashMap<>();
        counts.put(name, count);
        return counts;
    }
}
