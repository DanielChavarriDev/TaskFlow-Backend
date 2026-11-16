
package com.danielchavarri.taskflow.notification.infrastructure.persistence;

import com.danielchavarri.taskflow.notification.domain.model.Notification;
import com.danielchavarri.taskflow.notification.domain.model.NotificationType;
import com.danielchavarri.taskflow.notification.domain.port.NotificationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NotificationRepositoryAdapter
        implements NotificationRepository {

    private final SpringDataNotificationRepository repository;
    private final NotificationPersistenceMapper mapper;

    public NotificationRepositoryAdapter(
            SpringDataNotificationRepository repository,
            NotificationPersistenceMapper mapper) {

        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = mapper.toEntity(notification);

        return mapper.toDomain(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAllByUserId(UUID userId) {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> findByIdAndUserId(
            UUID notificationId,
            UUID userId) {

        return repository.findByIdAndUserId(notificationId, userId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTaskIdAndTypeAndDueDate(
            UUID taskId,
            NotificationType type,
            LocalDate dueDate) {

        return repository.existsByTaskIdAndTypeAndDueDate(
                taskId,
                type,
                dueDate);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUnreadByUserId(UUID userId) {
        return repository.countByUserIdAndReadAtIsNull(userId);
    }
}