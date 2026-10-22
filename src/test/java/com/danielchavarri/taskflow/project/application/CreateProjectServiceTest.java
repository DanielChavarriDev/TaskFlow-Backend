
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreateProjectServiceTest {

    @Test
    void createProjectSuccessfully() {
        ProjectRepository repository = mock(ProjectRepository.class);

        CreateProjectService service = new CreateProjectService(repository);

        when(repository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Project result = service.execute(
                "TaskFlow",
                "Gestión de proyectos y tareas");

        assertNotNull(result);
        assertEquals("TaskFlow", result.getName());
        assertEquals(
                "Gestión de proyectos y tareas",
                result.getDescription());

        verify(repository).save(any(Project.class));
    }
}