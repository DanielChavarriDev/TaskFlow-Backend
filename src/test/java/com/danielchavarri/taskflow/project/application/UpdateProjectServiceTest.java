
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
    void shouldUpdateExistingProject() {
        ProjectRepository repository = mock(ProjectRepository.class);

        UpdateProjectService service = new UpdateProjectService(repository);

        Project project = Project.create(
                "TaskFlow",
                "Descripción inicial");

        UUID id = project.getId();

        when(repository.findById(id))
                .thenReturn(Optional.of(project));

        when(repository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Project result = service.execute(
                id,
                "TaskFlow actualizado",
                "Descripción actualizada");

        assertEquals("TaskFlow actualizado", result.getName());
        assertEquals(
                "Descripción actualizada",
                result.getDescription());

        verify(repository).findById(id);
        verify(repository).save(project);
    }

    @Test
    void shouldThrowExceptionWhenProjectDoesNotExist() {
        ProjectRepository repository = mock(ProjectRepository.class);

        UpdateProjectService service = new UpdateProjectService(repository);

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> service.execute(
                        id,
                        "TaskFlow actualizado",
                        "Descripción actualizada"));

        verify(repository).findById(id);
        verify(repository, never()).save(any(Project.class));
    }
}