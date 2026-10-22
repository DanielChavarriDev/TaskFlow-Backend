
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChangeTaskStatusServiceTest {

    private TaskRepository taskRepository;
    private ChangeTaskStatusService changeTaskStatusService;

    private UUID taskId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        changeTaskStatusService = new ChangeTaskStatusService(taskRepository);

        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
    }

    @Test
    void shouldChangeTaskStatusWhenTaskExists() {
        Task task = Task.create(
                projectId,
                "Implementar pruebas",
                "Validar cambio de estado",
                TaskPriority.HIGH,
                null);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = changeTaskStatusService.execute(
                taskId,
                TaskStatus.IN_PROGRESS);

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void shouldChangeTaskStatusToCompleted() {
        Task task = Task.create(
                projectId,
                "Completar módulo",
                null,
                TaskPriority.MEDIUM,
                null);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = changeTaskStatusService.execute(
                taskId,
                TaskStatus.COMPLETED);

        assertEquals(TaskStatus.COMPLETED, result.getStatus());

        verify(taskRepository).save(task);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> changeTaskStatusService.execute(
                        taskId,
                        TaskStatus.IN_PROGRESS));

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }
}