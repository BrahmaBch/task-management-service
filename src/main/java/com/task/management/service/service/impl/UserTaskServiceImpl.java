package com.task.management.service.service.impl;

import com.task.management.service.models.UserTask;
import com.task.management.service.repository.TaskRepository;
import com.task.management.service.service.UserTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class UserTaskServiceImpl implements UserTaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<UserTask> getTasksByUserId(String userId) {
        log.info("Fetching tasks for user: {}", userId);
        List<UserTask> tasks = taskRepository.findByUserId(userId);
        if (tasks.isEmpty()) {
            log.warn("No tasks found for user: {}", userId);
        }
        return tasks;
    }

    public Page<UserTask> getTasksByUserId(String userId, Pageable pageable) {
        log.info("Fetching paginated tasks for user: {}", userId);
        Page<UserTask> tasks = taskRepository.findByUserId(userId, pageable);
        if (tasks.isEmpty()) {
            log.warn("No tasks found for user: {} on page: {}", userId, pageable.getPageNumber());
        }
        return tasks;
    }

    public Page<UserTask> getTasksByUserIdAndStatus(String userId, String status, Pageable pageable) {
        log.info("Fetching paginated tasks for user: {} with status: {}", userId, status);
        Page<UserTask> tasks = taskRepository.findByUserIdAndStatus(userId, status, pageable);
        if (tasks.isEmpty()) {
            log.warn("No tasks found for user: {} with status: {} on page: {}", userId, status, pageable.getPageNumber());
        }
        return tasks;
    }

    public UserTask createTask(UserTask task) {
        log.info("Creating task: {}", task);
        validateTask(task);
        UserTask createdTask = taskRepository.save(task);
        log.info("Task created with ID: {}", createdTask.getId());
        return createdTask;
    }

    public Optional<UserTask> getTaskById(String id) {
        log.info("Fetching task with ID: {}", id);
        Optional<UserTask> task = taskRepository.findById(id);
        if (task.isEmpty()) {
            log.warn("Task with ID: {} not found", id);
        }
        return task;
    }

    public UserTask updateTask(String id, UserTask task) {
        log.info("Updating task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            log.error("Task with ID: {} not found for update", id);
            throw new IllegalArgumentException("Task not found");
        }
        validateTaskUpdate(task);
        task.setId(id);
        UserTask updatedTask = taskRepository.save(task);
        log.info("Task updated with ID: {}", updatedTask.getId());
        return updatedTask;
    }

    public void deleteTask(String id) {
        log.info("Deleting task with ID: {}", id);
        if (!taskRepository.existsById(id)) {
            log.error("Task with ID: {} not found for deletion", id);
            throw new IllegalArgumentException("Task not found");
        }
        taskRepository.deleteById(id);
        log.info("Task with ID: {} has been deleted", id);
    }

    private void validateTask(UserTask task) {
        if (!StringUtils.hasText(task.getTitle())) {
            log.error("Validation failed: Title is required.");
            throw new IllegalArgumentException("Title is required.");
        }
        if (task.getDueDate() == null) {
            log.error("Validation failed: Due date is required.");
            throw new IllegalArgumentException("Due date is required.");
        }
        log.info("Task validation passed.");
    }

    private void validateTaskUpdate(UserTask task) {
        // Add validation logic for updates if necessary
        log.info("Task update validation passed.");
    }
}
