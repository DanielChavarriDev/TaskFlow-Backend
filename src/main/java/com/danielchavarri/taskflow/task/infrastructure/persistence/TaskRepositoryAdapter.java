
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskRepositoryAdapter implements TaskRepository {

    private final SpringDataTaskRepository springDataRepository;
    private final TaskPersistenceMapper mapper;

    public TaskRepositoryAdapter(
            SpringDataTaskRepository springDataRepository,
            TaskPersistenceMapper mapper) {
        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Task save(Task task) {
        TaskJpaEntity entity = mapper.toEntity(task);
        TaskJpaEntity savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Task> findByIdAndOwnerId(UUID id, UUID ownerId) {
        return springDataRepository.findByIdAndOwnerId(id, ownerId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findAllByOwnerId(UUID ownerId) {
        return springDataRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findByProjectIdAndOwnerId(
            UUID projectId,
            UUID ownerId) {
        return springDataRepository
                .findByProjectIdAndOwnerId(projectId, ownerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public int deleteByIdAndOwnerId(UUID id, UUID ownerId) {
        return springDataRepository.deleteByIdAndOwnerId(id, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Task> findByFiltersAndOwnerId(
            UUID ownerId,
            TaskStatus status,
            TaskPriority priority) {

        return springDataRepository.findByFiltersAndOwnerId(
                ownerId,
                status == null ? null : status.name(),
                priority == null ? null : priority.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}