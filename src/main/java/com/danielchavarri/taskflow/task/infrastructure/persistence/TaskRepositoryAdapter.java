
package com.danielchavarri.taskflow.task.infrastructure.persistence;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TaskRepositoryAdapter implements TaskRepository {

    private final SpringDataTaskRepository springDataRepository;
    private final TaskPersistenceMapper mapper;

    public TaskRepositoryAdapter(
            SpringDataTaskRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
        this.mapper = new TaskPersistenceMapper();
    }

    @Override
    public Task save(Task task) {
        TaskJpaEntity entity = mapper.toEntity(task);
        TaskJpaEntity savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return springDataRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Task> findByProjectId(UUID projectId) {
        return springDataRepository.findByProjectId(projectId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepository.deleteById(id);
    }
}