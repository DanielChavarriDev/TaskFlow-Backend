
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.task.application.ChangeTaskStatusService;
import com.danielchavarri.taskflow.task.application.CreateTaskService;
import com.danielchavarri.taskflow.task.application.DeleteTaskService;
import com.danielchavarri.taskflow.task.application.GetFilteredTasksService;
import com.danielchavarri.taskflow.task.application.GetTaskByIdService;
import com.danielchavarri.taskflow.task.application.GetTasksByProjectService;
import com.danielchavarri.taskflow.task.application.UpdateTaskService;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final CreateTaskService createTaskService;
    private final GetFilteredTasksService getFilteredTasksService;
    private final GetTaskByIdService getTaskByIdService;
    private final GetTasksByProjectService getTasksByProjectService;
    private final UpdateTaskService updateTaskService;
    private final ChangeTaskStatusService changeTaskStatusService;
    private final DeleteTaskService deleteTaskService;

    public TaskController(
            CreateTaskService createTaskService,
            GetFilteredTasksService getFilteredTasksService,
            GetTaskByIdService getTaskByIdService,
            GetTasksByProjectService getTasksByProjectService,
            UpdateTaskService updateTaskService,
            ChangeTaskStatusService changeTaskStatusService,
            DeleteTaskService deleteTaskService) {

        this.createTaskService = createTaskService;
        this.getFilteredTasksService = getFilteredTasksService;
        this.getTaskByIdService = getTaskByIdService;
        this.getTasksByProjectService = getTasksByProjectService;
        this.updateTaskService = updateTaskService;
        this.changeTaskStatusService = changeTaskStatusService;
        this.deleteTaskService = deleteTaskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        Task task = createTaskService.execute(
                request.projectId(),
                request.title(),
                request.description(),
                request.priority(),
                request.dueDate());

        return TaskResponse.from(task);
    }

    @GetMapping
    public List<TaskResponse> getAll(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) TaskPriority priority) {

        return getFilteredTasksService.execute(status, priority)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable UUID id) {
        return TaskResponse.from(getTaskByIdService.execute(id));
    }

    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getByProject(@PathVariable UUID projectId) {
        return getTasksByProjectService.execute(projectId)
                .stream()
                .map(TaskResponse::from)
                .toList();
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTaskRequest request) {

        Task task = updateTaskService.execute(
                id,
                request.title(),
                request.description(),
                request.priority(),
                request.dueDate());

        return TaskResponse.from(task);
    }

    @PatchMapping("/{id}/status")
    public TaskResponse changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody ChangeTaskStatusRequest request) {

        Task task = changeTaskStatusService.execute(id, request.status());

        return TaskResponse.from(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteTaskService.execute(id);
    }
}