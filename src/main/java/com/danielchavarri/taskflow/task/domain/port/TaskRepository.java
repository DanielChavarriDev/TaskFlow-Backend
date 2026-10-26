
package com.danielchavarri.taskflow.task.domain.port;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findByIdAndOwnerId(UUID id, UUID ownerId);

    List<Task> findAllByOwnerId(UUID ownerId);

    List<Task> findByProjectIdAndOwnerId(
            UUID projectId,
            UUID ownerId);

    int deleteByIdAndOwnerId(UUID id, UUID ownerId);

    List<Task> findByFiltersAndOwnerId(
            UUID ownerId,
            TaskStatus status,
            TaskPriority priority);
}