
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetAllTasksService {

    private final TaskRepository taskRepository;

    public GetAllTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> execute(UUID ownerId) {
        return taskRepository.findAllByOwnerId(ownerId);
    }
}