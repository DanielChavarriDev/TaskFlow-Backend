
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
        private UUID ownerId;

        @BeforeEach
        void setUp() {
                taskRepository = mock(TaskRepository.class);
                updateTaskService = new UpdateTaskService(taskRepository);

                taskId = UUID.randomUUID();
                projectId = UUID.randomUUID();
                ownerId = UUID.randomUUID();
        }

        @Test
        void shouldUpdateTaskWhenItBelongsToUser() {

                Task task = Task.reconstitute(
                                taskId,
                                projectId,
                                "Título anterior",
                                "Descripción anterior",
                                TaskStatus.PENDING,
                                TaskPriority.LOW,
                                null,
                                java.time.Instant.now());

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.of(task));

                when(taskRepository.save(any(Task.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                LocalDate dueDate = LocalDate.of(2026, 11, 30);

                Task result = updateTaskService.execute(
                                taskId,
                                ownerId,
                                "Título actualizado",
                                "Descripción actualizada",
                                TaskPriority.HIGH,
                                dueDate);

                assertNotNull(result);
                assertEquals(taskId, result.getId());
                assertEquals("Título actualizado", result.getTitle());
                assertEquals("Descripción actualizada", result.getDescription());
                assertEquals(TaskPriority.HIGH, result.getPriority());
                assertEquals(dueDate, result.getDueDate());

                verify(taskRepository)
                                .findByIdAndOwnerId(taskId, ownerId);

                verify(taskRepository).save(task);
        }

        @Test
        void shouldThrowExceptionWhenTaskDoesNotExistOrBelongToUser() {

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.empty());

                assertThrows(
                                TaskNotFoundException.class,
                                () -> updateTaskService.execute(
                                                taskId,
                                                ownerId,
                                                "Título actualizado",
                                                "Descripción actualizada",
                                                TaskPriority.HIGH,
                                                null));

                verify(taskRepository)
                                .findByIdAndOwnerId(taskId, ownerId);

                verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void shouldPreserveTaskStatusWhenUpdatingDetails() {

                Task task = Task.reconstitute(
                                taskId,
                                projectId,
                                "Título anterior",
                                "Descripción anterior",
                                TaskStatus.IN_PROGRESS,
                                TaskPriority.MEDIUM,
                                null,
                                java.time.Instant.now());

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.of(task));

                when(taskRepository.save(any(Task.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Task result = updateTaskService.execute(
                                taskId,
                                ownerId,
                                "Título actualizado",
                                "Descripción actualizada",
                                TaskPriority.HIGH,
                                LocalDate.of(2026, 11, 30));

                assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
                assertEquals("Título actualizado", result.getTitle());

                verify(taskRepository).save(task);
        }
}