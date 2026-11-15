
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;
import com.danielchavarri.taskflow.task.domain.port.TaskStatusAuditRepository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public class TaskStatusAuditRepositoryAdapter
        implements TaskStatusAuditRepository {

    private final SpringDataTaskStatusAuditRepository springDataRepository;
    private final TaskStatusAuditPersistenceMapper mapper;

    public TaskStatusAuditRepositoryAdapter(
            SpringDataTaskStatusAuditRepository springDataRepository,
            TaskStatusAuditPersistenceMapper mapper) {

        this.springDataRepository = springDataRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public TaskStatusAudit save(TaskStatusAudit audit) {

        TaskStatusAuditJpaEntity entity = mapper.toEntity(audit);

        TaskStatusAuditJpaEntity savedEntity = springDataRepository.save(entity);

        return mapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskStatusAudit> findAllByTaskId(UUID taskId) {

        return springDataRepository
                .findAllByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}