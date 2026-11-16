
package com.danielchavarri.taskflow.notification.domain.port;

import com.danielchavarri.taskflow.notification.domain.model.Notification;
import com.danielchavarri.taskflow.notification.domain.model.NotificationType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> findAllByUserId(UUID userId);

    Optional<Notification> findByIdAndUserId(
            UUID notificationId,
            UUID userId);

    boolean existsByTaskIdAndTypeAndDueDate(
            UUID taskId,
            NotificationType type,
            LocalDate dueDate);

    long countUnreadByUserId(UUID userId);
}