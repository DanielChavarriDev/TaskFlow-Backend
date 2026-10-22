package com.danielchavarri.taskflow.project.infrastructure.persistence;

import com.danielchavarri.taskflow.project.domain.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectPersistenceMapper {

    public ProjectJpaEntity toEntity(Project project) {
        return new ProjectJpaEntity(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt());
    }

    public Project toDomain(ProjectJpaEntity entity) {
        return Project.reconstitute(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getCreatedAt());
    }
}