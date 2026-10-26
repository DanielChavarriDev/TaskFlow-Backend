
package com.danielchavarri.taskflow.project.infrastructure.persistence;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final SpringDataProjectRepository jpaRepository;
    private final ProjectPersistenceMapper mapper;

    public ProjectRepositoryAdapter(
            SpringDataProjectRepository jpaRepository,
            ProjectPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Project save(Project project) {
        ProjectJpaEntity entity = mapper.toEntity(project);
        ProjectJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Project> findByIdAndOwnerId(
            UUID id,
            UUID ownerId) {
        return jpaRepository.findByIdAndOwnerId(id, ownerId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> findAllByOwnerId(UUID ownerId) {
        return jpaRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteByIdAndOwnerId(UUID id, UUID ownerId) {
        jpaRepository.deleteByIdAndOwnerId(id, ownerId);
    }
}