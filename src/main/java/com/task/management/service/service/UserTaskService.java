package com.task.management.service.service;

import com.task.management.service.models.UserTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserTaskService {

    List<UserTask> getTasksByUserId(String userId);

    Page<UserTask> getTasksByUserId(String userId, Pageable pageable);

    Page<UserTask> getTasksByUserIdAndStatus(String userId, String status, Pageable pageable);

    UserTask createTask(UserTask task);

    Optional<UserTask> getTaskById(String id);

    UserTask updateTask(String id, UserTask task);

    void deleteTask(String id);
}
