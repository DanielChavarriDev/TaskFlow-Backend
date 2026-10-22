
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
    void shouldReturnProjectWhenItExists() {
        ProjectRepository repository = mock(ProjectRepository.class);

        GetProjectByIdService service = new GetProjectByIdService(repository);

        UUID id = UUID.randomUUID();
        Project project = Project.create(
                "TaskFlow",
                "Gestión de proyectos y tareas");

        when(repository.findById(id))
                .thenReturn(Optional.of(project));

        Project result = service.execute(id);

        assertNotNull(result);
        assertEquals("TaskFlow", result.getName());
        verify(repository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenProjectDoesNotExist() {
        ProjectRepository repository = mock(ProjectRepository.class);

        GetProjectByIdService service = new GetProjectByIdService(repository);

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(
                ProjectNotFoundException.class,
                () -> service.execute(id));

        verify(repository).findById(id);
    }
}