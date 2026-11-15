
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.port.TaskRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeleteTaskServiceTest {

    private TaskRepository taskRepository;
    private DeleteTaskService deleteTaskService;

    private UUID taskId;
    private UUID ownerId;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        deleteTaskService = new DeleteTaskService(taskRepository);

        taskId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
    }

    @Test
    void shouldDeleteTaskWhenItBelongsToUser() {

        when(taskRepository.deleteByIdAndOwnerId(taskId, ownerId))
                .thenReturn(1);

        assertDoesNotThrow(
                () -> deleteTaskService.execute(taskId, ownerId));

        verify(taskRepository)
                .deleteByIdAndOwnerId(taskId, ownerId);
    }

    @Test
    void shouldThrowExceptionWhenTaskDoesNotExistOrBelongToUser() {

        when(taskRepository.deleteByIdAndOwnerId(taskId, ownerId))
                .thenReturn(0);

        assertThrows(
                TaskNotFoundException.class,
                () -> deleteTaskService.execute(taskId, ownerId));

        verify(taskRepository)
                .deleteByIdAndOwnerId(taskId, ownerId);
    }
}