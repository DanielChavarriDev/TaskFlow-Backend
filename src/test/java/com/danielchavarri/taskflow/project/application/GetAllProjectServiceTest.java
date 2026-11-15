
package com.danielchavarri.taskflow.project.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetAllProjectsServiceTest {

    @Test
    void shouldReturnAllProjectsOwnedByUser() {
        ProjectRepository repository = mock(ProjectRepository.class);
        GetAllProjectsService service = new GetAllProjectsService(repository);

        UUID ownerId = UUID.randomUUID();

        Project project1 = Project.create(
                ownerId,
                "TaskFlow",
                "Gestión de tareas");

        Project project2 = Project.create(
                ownerId,
                "Project Manager",
                "Gestión de proyectos");

        List<Project> projects = List.of(project1, project2);

        when(repository.findAllByOwnerId(ownerId))
                .thenReturn(projects);

        List<Project> result = service.execute(ownerId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(ownerId, result.get(0).getOwnerId());
        assertEquals(ownerId, result.get(1).getOwnerId());

        verify(repository).findAllByOwnerId(ownerId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoProjects() {
        ProjectRepository repository = mock(ProjectRepository.class);
        GetAllProjectsService service = new GetAllProjectsService(repository);

        UUID ownerId = UUID.randomUUID();

        when(repository.findAllByOwnerId(ownerId))
                .thenReturn(List.of());

        List<Project> result = service.execute(ownerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(repository).findAllByOwnerId(ownerId);
    }
}