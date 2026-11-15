
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateProjectServiceTest {

        @Test
        void shouldUpdateExistingProjectOwnedByUser() {
                ProjectRepository repository = mock(ProjectRepository.class);
                UpdateProjectService service = new UpdateProjectService(repository);

                UUID ownerId = UUID.randomUUID();

                Project project = Project.create(
                                ownerId,
                                "TaskFlow",
                                "Descripción inicial");

                UUID projectId = project.getId();

                when(repository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.of(project));

                when(repository.save(any(Project.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Project result = service.execute(
                                projectId,
                                ownerId,
                                "TaskFlow actualizado",
                                "Descripción actualizada");

                assertNotNull(result);
                assertEquals(projectId, result.getId());
                assertEquals(ownerId, result.getOwnerId());
                assertEquals("TaskFlow actualizado", result.getName());
                assertEquals(
                                "Descripción actualizada",
                                result.getDescription());

                verify(repository).findByIdAndOwnerId(projectId, ownerId);
                verify(repository).save(project);
        }

        @Test
        void shouldThrowExceptionWhenProjectDoesNotExistOrDoesNotBelongToUser() {
                ProjectRepository repository = mock(ProjectRepository.class);
                UpdateProjectService service = new UpdateProjectService(repository);

                UUID projectId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();

                when(repository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ProjectNotFoundException.class,
                                () -> service.execute(
                                                projectId,
                                                ownerId,
                                                "TaskFlow actualizado",
                                                "Descripción actualizada"));

                verify(repository).findByIdAndOwnerId(projectId, ownerId);
                verify(repository, never()).save(any(Project.class));
        }
}