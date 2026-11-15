
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.task.application.ApproveTaskCompletionService;
import com.danielchavarri.taskflow.task.application.CreateTaskService;
import com.danielchavarri.taskflow.task.application.DeleteTaskService;
import com.danielchavarri.taskflow.task.application.GetFilteredTasksService;
import com.danielchavarri.taskflow.task.application.GetTaskByIdService;
import com.danielchavarri.taskflow.task.application.GetTaskStatusAuditService;
import com.danielchavarri.taskflow.task.application.GetTasksByProjectService;
import com.danielchavarri.taskflow.task.application.RejectTaskCompletionService;
import com.danielchavarri.taskflow.task.application.RequestTaskCompletionService;
import com.danielchavarri.taskflow.task.application.StartTaskService;
import com.danielchavarri.taskflow.task.application.UpdateTaskService;
import com.danielchavarri.taskflow.task.application.AdminInterveneTaskService;
import com.danielchavarri.taskflow.task.application.GetTasksPendingReviewService;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        private final DeleteTaskService deleteTaskService;

        private final StartTaskService startTaskService;
        private final RequestTaskCompletionService requestTaskCompletionService;
        private final ApproveTaskCompletionService approveTaskCompletionService;
        private final RejectTaskCompletionService rejectTaskCompletionService;

        private final AdminInterveneTaskService adminInterveneTaskService;

        private final GetTaskStatusAuditService getTaskStatusAuditService;

        private final GetTasksPendingReviewService getTasksPendingReviewService;

        public TaskController(
                        CreateTaskService createTaskService,
                        GetFilteredTasksService getFilteredTasksService,
                        GetTaskByIdService getTaskByIdService,
                        GetTasksByProjectService getTasksByProjectService,
                        UpdateTaskService updateTaskService,
                        DeleteTaskService deleteTaskService,
                        StartTaskService startTaskService,
                        RequestTaskCompletionService requestTaskCompletionService,
                        ApproveTaskCompletionService approveTaskCompletionService,
                        RejectTaskCompletionService rejectTaskCompletionService,
                        AdminInterveneTaskService adminInterveneTaskService,
                        GetTaskStatusAuditService getTaskStatusAuditService,
                        GetTasksPendingReviewService getTasksPendingReviewService) {

                this.createTaskService = createTaskService;
                this.getFilteredTasksService = getFilteredTasksService;
                this.getTaskByIdService = getTaskByIdService;
                this.getTasksByProjectService = getTasksByProjectService;
                this.updateTaskService = updateTaskService;
                this.deleteTaskService = deleteTaskService;

                this.startTaskService = startTaskService;
                this.requestTaskCompletionService = requestTaskCompletionService;
                this.approveTaskCompletionService = approveTaskCompletionService;
                this.rejectTaskCompletionService = rejectTaskCompletionService;

                this.adminInterveneTaskService = adminInterveneTaskService;

                this.getTaskStatusAuditService = getTaskStatusAuditService;

                this.getTasksPendingReviewService = getTasksPendingReviewService;
        }

        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public TaskResponse create(
                        @Valid @RequestBody CreateTaskRequest request) {

                Task task = createTaskService.execute(
                                request.projectId(),
                                getAuthenticatedUserId(),
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

                return getFilteredTasksService.execute(
                                getAuthenticatedUserId(),
                                status,
                                priority)
                                .stream()
                                .map(TaskResponse::from)
                                .toList();
        }

        @GetMapping("/{id}")
        public TaskResponse getById(@PathVariable UUID id) {
                return TaskResponse.from(
                                getTaskByIdService.execute(
                                                id,
                                                getAuthenticatedUserId()));
        }

        @GetMapping("/project/{projectId}")
        public List<TaskResponse> getByProject(
                        @PathVariable UUID projectId) {

                return getTasksByProjectService.execute(
                                projectId,
                                getAuthenticatedUserId())
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
                                getAuthenticatedUserId(),
                                request.title(),
                                request.description(),
                                request.priority(),
                                request.dueDate());

                return TaskResponse.from(task);
        }

        @PatchMapping("/{id}/start")
        public TaskResponse start(@PathVariable UUID id) {
                Task task = startTaskService.execute(
                                id,
                                getAuthenticatedUserId());

                return TaskResponse.from(task);
        }

        @PatchMapping("/{id}/request-completion")
        public TaskResponse requestCompletion(
                        @PathVariable UUID id) {

                Task task = requestTaskCompletionService.execute(
                                id,
                                getAuthenticatedUserId());

                return TaskResponse.from(task);
        }

        @PatchMapping("/{id}/approve-completion")
        public TaskResponse approveCompletion(
                        @PathVariable UUID id,
                        @Valid @RequestBody AdminTaskCompletionDecisionRequest request) {

                Task task = approveTaskCompletionService.execute(
                                id,
                                getAuthenticatedUserId(),
                                request.reason());

                return TaskResponse.from(task);
        }

        @PatchMapping("/{id}/reject-completion")
        public TaskResponse rejectCompletion(
                        @PathVariable UUID id,
                        @Valid @RequestBody AdminTaskCompletionDecisionRequest request) {

                Task task = rejectTaskCompletionService.execute(
                                id,
                                getAuthenticatedUserId(),
                                request.reason());

                return TaskResponse.from(task);
        }

        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        public void delete(@PathVariable UUID id) {
                deleteTaskService.execute(
                                id,
                                getAuthenticatedUserId());
        }

        private UUID getAuthenticatedUserId() {
                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !(authentication.getPrincipal() instanceof UUID userId)
                                || !authentication.isAuthenticated()) {

                        throw new IllegalStateException(
                                        "No se pudo obtener el usuario autenticado.");
                }

                return userId;
        }

        @PatchMapping("/{id}/admin-intervention")
        public TaskResponse adminIntervention(
                        @PathVariable UUID id,
                        @Valid @RequestBody ChangeTaskStatusAdminRequest request) {

                Task task = adminInterveneTaskService.execute(
                                id,
                                getAuthenticatedUserId(),
                                request.newStatus(),
                                request.reason());

                return TaskResponse.from(task);
        }

        @GetMapping("/{id}/admin-interventions")
        public List<TaskStatusAuditResponse> getAdminInterventions(
                        @PathVariable UUID id) {

                return getTaskStatusAuditService.execute(id)
                                .stream()
                                .map(TaskStatusAuditResponse::from)
                                .toList();
        }

        @GetMapping("/admin/pending-review")
        public List<TaskResponse> getPendingReview() {
                return getTasksPendingReviewService.execute()
                                .stream()
                                .map(TaskResponse::from)
                                .toList();
        }
}