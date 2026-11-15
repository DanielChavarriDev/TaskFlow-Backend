
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateProjectServiceTest {

        @Test
        void shouldCreateProjectSuccessfully() {
                ProjectRepository repository = mock(ProjectRepository.class);
                CreateProjectService service = new CreateProjectService(repository);

                UUID ownerId = UUID.randomUUID();

                when(repository.save(any(Project.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Project result = service.execute(
                                ownerId,
                                "TaskFlow",
                                "Gestión de proyectos y tareas");

                assertNotNull(result);
                assertNotNull(result.getId());
                assertEquals(ownerId, result.getOwnerId());
                assertEquals("TaskFlow", result.getName());
                assertEquals(
                                "Gestión de proyectos y tareas",
                                result.getDescription());
                assertNotNull(result.getCreatedAt());

                verify(repository).save(any(Project.class));
        }
}