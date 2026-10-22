
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DeleteTaskServiceTest {

    private TaskRepository taskRepository;
    private DeleteTaskService deleteTaskService;

    private UUID taskId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        deleteTaskService = new DeleteTaskService(taskRepository);

        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
    }

    @Test
    void shouldDeleteTaskWhenTaskExists() {
        Task task = Task.reconstitute(
                taskId,
                projectId,
                "Tarea de prueba",
                "Tarea que será eliminada",
                com.danielchavarri.taskflow.task.domain.model.TaskStatus.PENDING,
                TaskPriority.MEDIUM,
                null,
                java.time.Instant.now());

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        deleteTaskService.execute(taskId);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> deleteTaskService.execute(taskId));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).deleteById(any(UUID.class));
    }
}