
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteTaskService {

    private final TaskRepository taskRepository;

    public DeleteTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void execute(UUID id, UUID ownerId) {
        int deletedRows = taskRepository.deleteByIdAndOwnerId(id, ownerId);

        if (deletedRows == 0) {
            throw new TaskNotFoundException(id);
        }
    }
}