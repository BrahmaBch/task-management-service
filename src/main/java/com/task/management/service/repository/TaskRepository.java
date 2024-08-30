package com.task.management.service.repository;

import com.task.management.service.models.UserTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TaskRepository extends MongoRepository<UserTask, String> {
    List<UserTask> findByUserId(String userId);
    Page<UserTask> findByUserId(String userId, Pageable pageable);
    List<UserTask> findByUserIdAndStatus(String userId, String status);
    Page<UserTask> findByUserIdAndStatus(String userId, String status, Pageable pageable);
}
