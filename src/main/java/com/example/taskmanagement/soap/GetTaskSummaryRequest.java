package com.example.taskmanagement.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
        name = "GetTaskSummaryRequest",
        namespace = TaskSummaryEndpoint.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTaskSummaryRequest {
}
