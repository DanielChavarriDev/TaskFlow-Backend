
package com.danielchavarri.taskflow.notification.application;

import com.danielchavarri.taskflow.notification.domain.model.Notification;
import com.danielchavarri.taskflow.notification.domain.model.NotificationType;
import com.danielchavarri.taskflow.notification.domain.port.NotificationRepository;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GenerateTaskNotificationsService {

    private static final int UPCOMING_DAYS = 3;

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final NotificationRepository notificationRepository;

    public GenerateTaskNotificationsService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            NotificationRepository notificationRepository) {

        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public void execute() {
        LocalDate today = LocalDate.now(
                java.time.ZoneId.of("America/Lima"));

        List<TaskStatus> activeStatuses = List.of(
                TaskStatus.PENDING,
                TaskStatus.IN_PROGRESS,
                TaskStatus.IN_REVIEW);

        for (TaskStatus status : activeStatuses) {
            List<Task> tasks = taskRepository.findAllByStatus(status);

            for (Task task : tasks) {
                processTask(task, today);
            }
        }
    }

    private void processTask(Task task, LocalDate today) {

        if (task.getDueDate() == null) {
            return;
        }

        LocalDate dueDate = task.getDueDate();
        long daysUntilDue = ChronoUnit.DAYS.between(today, dueDate);

        Optional<NotificationType> notificationType = determineNotificationType(daysUntilDue);

        if (notificationType.isEmpty()) {
            return;
        }

        UUID ownerId = projectRepository
                .findOwnerIdByProjectId(task.getProjectId())
                .orElse(null);

        if (ownerId == null) {
            return;
        }

        NotificationType type = notificationType.get();

        boolean alreadyExists = notificationRepository.existsByTaskIdAndTypeAndDueDate(
                task.getId(),
                type,
                dueDate);

        if (alreadyExists) {
            return;
        }

        String title = switch (type) {
            case UPCOMING -> "Próximo vencimiento";
            case DUE_TODAY -> "La tarea vence hoy";
            case OVERDUE -> "Tarea vencida";
        };

        String message = switch (type) {
            case UPCOMING ->
                "La tarea \"" + task.getTitle()
                        + "\" vence el " + dueDate + ".";

            case DUE_TODAY ->
                "La tarea \"" + task.getTitle()
                        + "\" vence hoy.";

            case OVERDUE ->
                "La tarea \"" + task.getTitle()
                        + "\" está vencida desde el " + dueDate + ".";
        };

        Notification notification = Notification.create(
                ownerId,
                task.getId(),
                type,
                title,
                message,
                dueDate);

        notificationRepository.save(notification);
    }

    private Optional<NotificationType> determineNotificationType(
            long daysUntilDue) {

        if (daysUntilDue < 0) {
            return Optional.of(NotificationType.OVERDUE);
        }

        if (daysUntilDue == 0) {
            return Optional.of(NotificationType.DUE_TODAY);
        }

        if (daysUntilDue <= UPCOMING_DAYS) {
            return Optional.of(NotificationType.UPCOMING);
        }

        return Optional.empty();
    }
}