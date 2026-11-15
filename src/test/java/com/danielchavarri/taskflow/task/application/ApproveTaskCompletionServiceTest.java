package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import com.danielchavarri.taskflow.task.domain.port.TaskStatusAuditRepository;
import com.danielchavarri.taskflow.task.domain.model.TaskStatusConflictException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveTaskCompletionServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStatusAuditRepository auditRepository;

    private ApproveTaskCompletionService service;

    private UUID taskId;
    private UUID adminUserId;
    private Task task;

    @BeforeEach
    void setUp() {
        service = new ApproveTaskCompletionService(
                taskRepository,
                auditRepository);

        taskId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();

        task = Task.reconstitute(
                taskId,
                UUID.randomUUID(),
                "Implementar autenticación",
                "Desarrollar autenticación JWT",
                TaskStatus.IN_REVIEW,
                TaskPriority.HIGH,
                LocalDate.now().plusDays(5),
                java.time.Instant.now());
    }

    @Test
    void shouldApproveTaskAndRegisterAudit() {

        when(taskRepository.findByIdForUpdate(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = service.execute(
                taskId,
                adminUserId,
                "Se verificó el cumplimiento de los criterios.");

        assertEquals(TaskStatus.COMPLETED, result.getStatus());

        verify(taskRepository).findByIdForUpdate(taskId);
        verify(taskRepository).save(task);

        verify(auditRepository).save(argThat(audit -> audit.taskId().equals(taskId)
                && audit.adminUserId().equals(adminUserId)
                && audit.previousStatus() == TaskStatus.IN_REVIEW
                && audit.newStatus() == TaskStatus.COMPLETED
                && audit.reason().equals(
                        "Se verificó el cumplimiento de los criterios.")));

    }

    @Test
    void shouldNotRegisterAuditWhenTaskDoesNotExist() {

        when(taskRepository.findByIdForUpdate(taskId))
                .thenReturn(Optional.empty());

        assertThrows(
                TaskNotFoundException.class,
                () -> service.execute(
                        taskId,
                        adminUserId,
                        "Aprobación justificada."));

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(auditRepository);
    }

    @Test
    void shouldNotRegisterAuditWhenTaskCannotBeApproved() {

        task = Task.reconstitute(
                taskId,
                UUID.randomUUID(),
                "Implementar autenticación",
                null,
                TaskStatus.PENDING,
                TaskPriority.HIGH,
                null,
                java.time.Instant.now());

        when(taskRepository.findByIdForUpdate(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                TaskStatusConflictException.class,
                () -> service.execute(
                        taskId,
                        adminUserId,
                        "Aprobación justificada."));

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(auditRepository);
    }

    @Test
    void shouldRejectBlankReason() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(taskId, adminUserId, "   "));

        verifyNoInteractions(taskRepository, auditRepository);
    }

    @Test
    void shouldRejectReasonLongerThan500Characters() {

        String reason = "A".repeat(501);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(taskId, adminUserId, reason));

        verifyNoInteractions(taskRepository, auditRepository);
    }
}