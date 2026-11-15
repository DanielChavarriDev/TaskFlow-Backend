
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetProjectByIdServiceTest {

        @Test
        void shouldReturnProjectWhenItExistsAndBelongsToUser() {
                ProjectRepository repository = mock(ProjectRepository.class);
                GetProjectByIdService service = new GetProjectByIdService(repository);

                UUID ownerId = UUID.randomUUID();

                Project project = Project.create(
                                ownerId,
                                "TaskFlow",
                                "Gestión de proyectos y tareas");

                UUID projectId = project.getId();

                when(repository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.of(project));

                Project result = service.execute(projectId, ownerId);

                assertNotNull(result);
                assertEquals(projectId, result.getId());
                assertEquals(ownerId, result.getOwnerId());
                assertEquals("TaskFlow", result.getName());

                verify(repository).findByIdAndOwnerId(projectId, ownerId);
        }

        @Test
        void shouldThrowExceptionWhenProjectDoesNotExistOrDoesNotBelongToUser() {
                ProjectRepository repository = mock(ProjectRepository.class);
                GetProjectByIdService service = new GetProjectByIdService(repository);

                UUID projectId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();

                when(repository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ProjectNotFoundException.class,
                                () -> service.execute(projectId, ownerId));

                verify(repository).findByIdAndOwnerId(projectId, ownerId);
        }
}