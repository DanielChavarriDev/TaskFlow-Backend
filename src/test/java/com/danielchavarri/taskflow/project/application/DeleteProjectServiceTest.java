
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
    void shouldDeleteExistingProject() {
        ProjectRepository repository = mock(ProjectRepository.class);

        DeleteProjectService service = new DeleteProjectService(repository);

        Project project = Project.create(
                "Proyecto de prueba",
                "Proyecto para probar la eliminación");

        UUID id = project.getId();

        when(repository.findById(id))
                .thenReturn(Optional.of(project));

        service.execute(id);

        verify(repository).findById(id);
        verify(repository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenProjectDoesNotExist() {
        ProjectRepository repository = mock(ProjectRepository.class);

        DeleteProjectService service = new DeleteProjectService(repository);

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> service.execute(id));

        verify(repository).findById(id);
        verify(repository, never()).deleteById(id);
    }
}