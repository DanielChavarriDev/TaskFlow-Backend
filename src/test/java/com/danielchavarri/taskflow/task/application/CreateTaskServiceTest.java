
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.project.domain.port.ProjectRepository;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateTaskServiceTest {

    private TaskRepository taskRepository;
    private ProjectRepository projectRepository;
    private CreateTaskService createTaskService;

    private UUID projectId;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        projectRepository = mock(ProjectRepository.class);

        createTaskService = new CreateTaskService(
                taskRepository,
                projectRepository);

        projectId = UUID.randomUUID();
    }

    @Test
    void shouldCreateTaskWhenProjectExists() {
        Project project = Project.create(
                "Proyecto de prueba",
                "Descripción del proyecto");

        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(project));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = createTaskService.execute(
                projectId,
                "Implementar pruebas",
                "Crear pruebas unitarias",
                TaskPriority.HIGH,
                LocalDate.of(2026, 10, 30));

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(projectId, result.getProjectId());
        assertEquals("Implementar pruebas", result.getTitle());
        assertEquals(
                "Crear pruebas unitarias",
                result.getDescription());
        assertEquals(TaskPriority.HIGH, result.getPriority());
        assertEquals(TaskStatus.PENDING, result.getStatus());
        assertEquals(
                LocalDate.of(2026, 10, 30),
                result.getDueDate());

        verify(projectRepository).findById(projectId);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldRejectCreationWhenProjectDoesNotExist() {
        when(projectRepository.findById(projectId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createTaskService.execute(
                        projectId,
                        "Implementar pruebas",
                        "Crear pruebas unitarias",
                        TaskPriority.HIGH,
                        null));

        assertTrue(
                exception.getMessage().contains(
                        "No existe el proyecto con ID: " + projectId));

        verify(projectRepository).findById(projectId);
        verify(taskRepository, never()).save(any(Task.class));
    }
}