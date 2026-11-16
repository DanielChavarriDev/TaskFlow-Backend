
package com.danielchavarri.taskflow.notification.infrastructure.scheduler;

import com.danielchavarri.taskflow.notification.application.GenerateTaskNotificationsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TaskNotificationScheduler {

    private final GenerateTaskNotificationsService service;

    public TaskNotificationScheduler(
            GenerateTaskNotificationsService service) {
        this.service = service;
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "America/Lima")
    public void generateDailyNotifications() {
        service.execute();
    }
}