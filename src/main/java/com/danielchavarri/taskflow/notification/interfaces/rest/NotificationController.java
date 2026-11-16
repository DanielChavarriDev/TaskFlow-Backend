
package com.danielchavarri.taskflow.notification.interfaces.rest;

import com.danielchavarri.taskflow.notification.application.GetUserNotificationsService;
import com.danielchavarri.taskflow.notification.application.MarkNotificationAsReadService;
import com.danielchavarri.taskflow.notification.domain.model.Notification;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final GetUserNotificationsService getUserNotificationsService;
    private final MarkNotificationAsReadService markNotificationAsReadService;

    public NotificationController(
            GetUserNotificationsService getUserNotificationsService,
            MarkNotificationAsReadService markNotificationAsReadService) {

        this.getUserNotificationsService = getUserNotificationsService;
        this.markNotificationAsReadService = markNotificationAsReadService;
    }

    @GetMapping
    public List<NotificationResponse> getNotifications(
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        return getUserNotificationsService.execute(userId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse getUnreadCount(
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        long count = getUserNotificationsService.getUnreadCount(userId);

        return new UnreadCountResponse(count);
    }

    @PatchMapping("/{notificationId}/read")
    public NotificationResponse markAsRead(
            @PathVariable UUID notificationId,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();

        Notification notification = markNotificationAsReadService.execute(
                notificationId,
                userId);

        return NotificationResponse.from(notification);
    }

    public record UnreadCountResponse(long unreadCount) {
    }
}