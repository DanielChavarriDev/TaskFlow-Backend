
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
        private UUID ownerId;

        @BeforeEach
        void setUp() {
                taskRepository = mock(TaskRepository.class);
                projectRepository = mock(ProjectRepository.class);

                createTaskService = new CreateTaskService(
                                taskRepository,
                                projectRepository);

                projectId = UUID.randomUUID();
                ownerId = UUID.randomUUID();
        }

        @Test
        void shouldCreateTaskWhenProjectBelongsToUser() {

                Project project = Project.reconstitute(
                                projectId,
                                ownerId,
                                "Proyecto de prueba",
                                "Descripción del proyecto",
                                java.time.Instant.now());

                when(projectRepository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.of(project));

                when(taskRepository.save(any(Task.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                LocalDate dueDate = LocalDate.of(2026, 11, 30);

                Task result = createTaskService.execute(
                                projectId,
                                ownerId,
                                "Implementar pruebas",
                                "Crear pruebas unitarias",
                                TaskPriority.HIGH,
                                dueDate);

                assertNotNull(result);
                assertNotNull(result.getId());
                assertEquals(projectId, result.getProjectId());
                assertEquals("Implementar pruebas", result.getTitle());
                assertEquals("Crear pruebas unitarias", result.getDescription());
                assertEquals(TaskPriority.HIGH, result.getPriority());
                assertEquals(TaskStatus.PENDING, result.getStatus());
                assertEquals(dueDate, result.getDueDate());

                verify(projectRepository)
                                .findByIdAndOwnerId(projectId, ownerId);

                verify(taskRepository).save(any(Task.class));
        }

        @Test
        void shouldRejectCreationWhenProjectDoesNotExistOrBelongToUser() {

                when(projectRepository.findByIdAndOwnerId(projectId, ownerId))
                                .thenReturn(Optional.empty());

                IllegalArgumentException exception = assertThrows(
                                IllegalArgumentException.class,
                                () -> createTaskService.execute(
                                                projectId,
                                                ownerId,
                                                "Implementar pruebas",
                                                "Crear pruebas unitarias",
                                                TaskPriority.HIGH,
                                                null));

                assertTrue(exception.getMessage().contains(projectId.toString()));

                verify(projectRepository)
                                .findByIdAndOwnerId(projectId, ownerId);

                verify(taskRepository, never()).save(any(Task.class));
        }
}