
package com.danielchavarri.taskflow.task.domain.port;

import com.danielchavarri.taskflow.task.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findAll();

    List<Task> findByProjectId(UUID projectId);

    void deleteById(UUID id);
}