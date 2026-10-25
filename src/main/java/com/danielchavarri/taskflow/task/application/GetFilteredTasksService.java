package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetFilteredTasksService {

    private final TaskRepository taskRepository;

    public GetFilteredTasksService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> execute(
            TaskStatus status,
            TaskPriority priority) {

        return taskRepository.findByFilters(status, priority);
    }
}