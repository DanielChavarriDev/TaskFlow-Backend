
package com.danielchavarri.taskflow.notification.application;

import com.danielchavarri.taskflow.notification.domain.model.Notification;
import com.danielchavarri.taskflow.notification.domain.port.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class GetUserNotificationsService {

    private final NotificationRepository notificationRepository;

    public GetUserNotificationsService(
            NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional(readOnly = true)
    public List<Notification> execute(UUID userId) {
        return notificationRepository.findAllByUserId(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }
}