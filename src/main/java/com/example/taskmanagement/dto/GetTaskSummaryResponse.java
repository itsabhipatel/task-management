package com.example.taskmanagement.dto;

import com.example.taskmanagement.controller.TaskSummaryEndpoint;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(
        name = "GetTaskSummaryResponse",
        namespace = TaskSummaryEndpoint.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTaskSummaryResponse {

    private long totalTasks;
    private long overdueTasks;
    private double averageProgress;

    @XmlElement(name = "statusCounts")
    private List<SummaryCount> statusCounts = new ArrayList<>();

    @XmlElement(name = "priorityCounts")
    private List<SummaryCount> priorityCounts = new ArrayList<>();

    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getOverdueTasks() {
        return overdueTasks;
    }

    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }

    public double getAverageProgress() {
        return averageProgress;
    }

    public void setAverageProgress(double averageProgress) {
        this.averageProgress = averageProgress;
    }

    public List<SummaryCount> getStatusCounts() {
        return statusCounts;
    }

    public void setStatusCounts(List<SummaryCount> statusCounts) {
        this.statusCounts = statusCounts;
    }

    public List<SummaryCount> getPriorityCounts() {
        return priorityCounts;
    }

    public void setPriorityCounts(List<SummaryCount> priorityCounts) {
        this.priorityCounts = priorityCounts;
    }
}
