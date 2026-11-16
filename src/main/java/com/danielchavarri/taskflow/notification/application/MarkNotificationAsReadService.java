
package com.danielchavarri.taskflow.notification.application;

import com.danielchavarri.taskflow.notification.domain.model.Notification;
import com.danielchavarri.taskflow.notification.domain.port.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MarkNotificationAsReadService {

    private final NotificationRepository notificationRepository;

    public MarkNotificationAsReadService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification execute(UUID notificationId, UUID userId) {

        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Notificación no encontrada"));

        notification.markAsRead();

        return notificationRepository.save(notification);
    }
}