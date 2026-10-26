
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetFilteredTasksService {

    private final TaskRepository taskRepository;

    public GetFilteredTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> execute(
            UUID ownerId,
            TaskStatus status,
            TaskPriority priority) {

        return taskRepository.findByFiltersAndOwnerId(
                ownerId,
                status,
                priority);
    }
}