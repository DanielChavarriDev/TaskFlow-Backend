
package com.danielchavarri.taskflow.notification.infrastructure.persistence;

import com.danielchavarri.taskflow.notification.domain.model.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationPersistenceMapper {

    public NotificationJpaEntity toEntity(Notification notification) {
        return new NotificationJpaEntity(
                notification.getId(),
                notification.getUserId(),
                notification.getTaskId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getDueDate(),
                notification.getCreatedAt(),
                notification.getReadAt());
    }

    public Notification toDomain(NotificationJpaEntity entity) {
        return Notification.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getTaskId(),
                entity.getType(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getDueDate(),
                entity.getCreatedAt(),
                entity.getReadAt());
    }
}