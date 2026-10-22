
package com.danielchavarri.taskflow.task.application;

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

class UpdateTaskServiceTest {

    private TaskRepository taskRepository;
    private UpdateTaskService updateTaskService;

    private UUID taskId;
    private UUID projectId;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        updateTaskService = new UpdateTaskService(taskRepository);

        taskId = UUID.randomUUID();
        projectId = UUID.randomUUID();
    }

    @Test
    void shouldUpdateTaskWhenTaskExists() {
        Task task = Task.create(
                projectId,
                "Título anterior",
                "Descripción anterior",
                TaskPriority.LOW,
                null);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = updateTaskService.execute(
                taskId,
                "Título actualizado",
                "Descripción actualizada",
                TaskPriority.HIGH,
                LocalDate.of(2026, 11, 15));

        assertNotNull(result);
        assertEquals("Título actualizado", result.getTitle());
        assertEquals(
                "Descripción actualizada",
                result.getDescription());
        assertEquals(TaskPriority.HIGH, result.getPriority());
        assertEquals(
                LocalDate.of(2026, 11, 15),
                result.getDueDate());

        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExist() {
        when(taskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> updateTaskService.execute(
                        taskId,
                        "Título actualizado",
                        "Descripción actualizada",
                        TaskPriority.HIGH,
                        null));

        assertNotNull(exception);

        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldPreserveTaskStatusWhenUpdatingDetails() {
        Task task = Task.create(
                projectId,
                "Título anterior",
                "Descripción anterior",
                TaskPriority.MEDIUM,
                null);

        task.changeStatus(TaskStatus.IN_PROGRESS);

        when(taskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = updateTaskService.execute(
                taskId,
                "Título actualizado",
                "Descripción actualizada",
                TaskPriority.HIGH,
                LocalDate.of(2026, 11, 15));

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Título actualizado", result.getTitle());

        verify(taskRepository).save(task);
    }
}