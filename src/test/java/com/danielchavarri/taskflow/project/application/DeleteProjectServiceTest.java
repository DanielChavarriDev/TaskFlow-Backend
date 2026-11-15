
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteProjectServiceTest {

        @Test
        void shouldDeleteExistingProjectOwnedByUser() {
                ProjectRepository repository = mock(ProjectRepository.class);
                DeleteProjectService service = new DeleteProjectService(repository);

                UUID ownerId = UUID.randomUUID();

                Project project = Project.create(
                                ownerId,
                                "TaskFlow",
                                "Proyecto para probar la eliminación");

                UUID projectId = project.getId();

                when(repository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.of(project));

                service.execute(projectId, ownerId);

                verify(repository).findByIdAndOwnerId(projectId, ownerId);
                verify(repository).deleteByIdAndOwnerId(projectId, ownerId);
        }

        @Test
        void shouldThrowExceptionWhenProjectDoesNotExistOrDoesNotBelongToUser() {
                ProjectRepository repository = mock(ProjectRepository.class);
                DeleteProjectService service = new DeleteProjectService(repository);

                UUID projectId = UUID.randomUUID();
                UUID ownerId = UUID.randomUUID();

                when(repository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.empty());

                assertThrows(
                                ProjectNotFoundException.class,
                                () -> service.execute(projectId, ownerId));

                verify(repository).findByIdAndOwnerId(projectId, ownerId);
                verify(repository, never())
                                .deleteByIdAndOwnerId(any(UUID.class), any(UUID.class));
        }
}