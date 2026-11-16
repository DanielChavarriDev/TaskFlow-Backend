
package com.danielchavarri.taskflow.notification.infrastructure.persistence;

import com.danielchavarri.taskflow.notification.domain.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataNotificationRepository
        extends JpaRepository<NotificationJpaEntity, UUID> {

    List<NotificationJpaEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<NotificationJpaEntity> findByIdAndUserId(
            UUID id,
            UUID userId);

    boolean existsByTaskIdAndTypeAndDueDate(
            UUID taskId,
            NotificationType type,
            LocalDate dueDate);

    long countByUserIdAndReadAtIsNull(UUID userId);
}