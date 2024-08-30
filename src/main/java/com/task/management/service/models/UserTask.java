package com.task.management.service.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Data
@Document(collection = "tasks")
public class UserTask {

    @Id
    private String id;
    private String userId;
    private String title;
    private String description;
    private LocalDate dueDate;
    private String status; // e.g., "pending", "in-progress", "completed"
}
