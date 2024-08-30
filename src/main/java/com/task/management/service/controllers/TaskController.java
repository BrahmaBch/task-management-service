package com.task.management.service.controllers;


import com.task.management.service.models.UserTask;
import com.task.management.service.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600) // Allow cross-origin requests from any origin for 1 hour
@RestController // Indicate that this class is a REST controller
@RequestMapping("/api/task") // Base URL for task-related endpoints
public class TaskController {

    @Autowired
    private UserTaskService taskService;

    @GetMapping("/get-all-tasks")
    public Page<UserTask> getAllTasks(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sort", defaultValue = "dueDate") String sort,
            Principal principal
    ) {
        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by(sort));
        if (status != null) {
            return taskService.getTasksByUserIdAndStatus(principal.getName(), status, pageable);
        } else {
            return taskService.getTasksByUserId(principal.getName(), pageable);
        }
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('USER') or " + // Require USER, MODERATOR, or ADMIN role
            "hasRole('MODERATOR') or " +
            "hasRole('ADMIN')")
    public UserTask createTask(@RequestBody UserTask task, Principal principal) {
        task.setUserId(principal.getName());
        return taskService.createTask(task);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<UserTask> getTaskById(@PathVariable String id, Principal principal) {
        Optional<UserTask> task = taskService.getTaskById(id);
        if (task.isPresent() && task.get().getUserId().equals(principal.getName())) {
            return ResponseEntity.ok(task.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getByUserId/{userId}")
    public ResponseEntity<List<UserTask>> getTasksByUserId(@PathVariable String userId, Principal principal) {
        // Ensure the userId matches the authenticated user
        if (!userId.equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UserTask> tasks = taskService.getTasksByUserId(userId);
        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/update-task/{id}")
    public ResponseEntity<UserTask> updateTask(@PathVariable String id, @RequestBody UserTask task, Principal principal) {
        Optional<UserTask> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent() && existingTask.get().getUserId().equals(principal.getName())) {
            task.setId(id);
            return ResponseEntity.ok(taskService.updateTask(id, task));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete-task/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id, Principal principal) {
        Optional<UserTask> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent() && existingTask.get().getUserId().equals(principal.getName())) {
            taskService.deleteTask(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
