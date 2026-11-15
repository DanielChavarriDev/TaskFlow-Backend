
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
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
        private UUID ownerId;

        @BeforeEach
        void setUp() {
                taskRepository = mock(TaskRepository.class);
                changeTaskStatusService = new ChangeTaskStatusService(taskRepository);

                taskId = UUID.randomUUID();
                projectId = UUID.randomUUID();
                ownerId = UUID.randomUUID();
        }

        private Task createTask(TaskStatus status) {
                return Task.reconstitute(
                                taskId,
                                projectId,
                                "Tarea de prueba",
                                "Descripción de prueba",
                                status,
                                TaskPriority.HIGH,
                                null,
                                Instant.now());
        }

        @Test
        void shouldChangePendingTaskToInProgress() {

                Task task = createTask(TaskStatus.PENDING);

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.of(task));

                when(taskRepository.save(any(Task.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Task result = changeTaskStatusService.execute(
                                taskId,
                                ownerId,
                                TaskStatus.IN_PROGRESS);

                assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());

                verify(taskRepository)
                                .findByIdAndOwnerId(taskId, ownerId);

                verify(taskRepository).save(task);
        }

        @Test
        void shouldChangeInProgressTaskToInReview() {

                Task task = createTask(TaskStatus.IN_PROGRESS);

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.of(task));

                when(taskRepository.save(any(Task.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Task result = changeTaskStatusService.execute(
                                taskId,
                                ownerId,
                                TaskStatus.IN_REVIEW);

                assertEquals(TaskStatus.IN_REVIEW, result.getStatus());

                verify(taskRepository).save(task);
        }

        @Test
        void shouldReturnTaskToInProgressWhenReviewIsRejected() {

                Task task = createTask(TaskStatus.IN_REVIEW);

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.of(task));

                when(taskRepository.save(any(Task.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                Task result = changeTaskStatusService.execute(
                                taskId,
                                ownerId,
                                TaskStatus.IN_PROGRESS);

                assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());

                verify(taskRepository).save(task);
        }

        @Test
        void shouldThrowExceptionWhenTaskDoesNotExistOrBelongToUser() {

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.empty());

                assertThrows(
                                TaskNotFoundException.class,
                                () -> changeTaskStatusService.execute(
                                                taskId,
                                                ownerId,
                                                TaskStatus.IN_PROGRESS));

                verify(taskRepository)
                                .findByIdAndOwnerId(taskId, ownerId);

                verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void shouldNotAllowDirectTransitionToCompleted() {

                Task task = createTask(TaskStatus.IN_PROGRESS);

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.of(task));

                assertThrows(
                                IllegalArgumentException.class,
                                () -> changeTaskStatusService.execute(
                                                taskId,
                                                ownerId,
                                                TaskStatus.COMPLETED));

                verify(taskRepository, never()).save(any(Task.class));
        }

        @Test
        void shouldNotAllowTransitionToPending() {

                Task task = createTask(TaskStatus.IN_PROGRESS);

                when(taskRepository.findByIdAndOwnerId(taskId, ownerId))
                                .thenReturn(Optional.of(task));

                assertThrows(
                                IllegalArgumentException.class,
                                () -> changeTaskStatusService.execute(
                                                taskId,
                                                ownerId,
                                                TaskStatus.PENDING));

                verify(taskRepository, never()).save(any(Task.class));
        }
}