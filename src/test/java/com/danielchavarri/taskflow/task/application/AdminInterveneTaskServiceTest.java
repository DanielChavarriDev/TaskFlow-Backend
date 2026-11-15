
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.model.TaskStatusConflictException;
import com.danielchavarri.taskflow.task.domain.port.TaskRepository;
import com.danielchavarri.taskflow.task.domain.port.TaskStatusAuditRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInterveneTaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskStatusAuditRepository auditRepository;

    private AdminInterveneTaskService service;

    private UUID taskId;
    private UUID adminUserId;
    private Task task;

    @BeforeEach
    void setUp() {
        service = new AdminInterveneTaskService(
                taskRepository,
                auditRepository);

        taskId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();

        task = Task.reconstitute(
                taskId,
                UUID.randomUUID(),
                "Implementar autenticación",
                "Desarrollar autenticación JWT",
                TaskStatus.PENDING,
                TaskPriority.HIGH,
                null,
                Instant.now());
    }

    @Test
    void shouldInterveneTaskAndRegisterAudit() {

        when(taskRepository.findByIdForUpdate(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Task result = service.execute(
                taskId,
                adminUserId,
                TaskStatus.IN_PROGRESS,
                "Se autoriza iniciar la tarea.");

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());

        verify(taskRepository).findByIdForUpdate(taskId);
        verify(taskRepository).save(task);

        verify(auditRepository).save(argThat(audit -> audit.taskId().equals(taskId)
                && audit.adminUserId().equals(adminUserId)
                && audit.previousStatus() == TaskStatus.PENDING
                && audit.newStatus() == TaskStatus.IN_PROGRESS
                && audit.reason().equals(
                        "Se autoriza iniciar la tarea.")
                && audit.id() != null
                && audit.createdAt() != null));
    }

    @Test
    void shouldNormalizeReasonBeforeRegisteringAudit() {

        when(taskRepository.findByIdForUpdate(taskId))
                .thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.execute(
                taskId,
                adminUserId,
                TaskStatus.IN_PROGRESS,
                "  Motivo administrativo válido.  ");

        verify(auditRepository).save(argThat(audit -> audit.reason().equals("Motivo administrativo válido.")));
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
                        TaskStatus.IN_PROGRESS,
                        "Intervención justificada."));

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(auditRepository);
    }

    @Test
    void shouldNotRegisterAuditWhenTransitionIsNotAllowed() {

        when(taskRepository.findByIdForUpdate(taskId))
                .thenReturn(Optional.of(task));

        assertThrows(
                TaskStatusConflictException.class,
                () -> service.execute(
                        taskId,
                        adminUserId,
                        TaskStatus.COMPLETED,
                        "Cambio administrativo."));

        assertEquals(TaskStatus.PENDING, task.getStatus());

        verify(taskRepository, never()).save(any());
        verifyNoInteractions(auditRepository);
    }

    @Test
    void shouldRejectBlankReason() {

        assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(
                        taskId,
                        adminUserId,
                        TaskStatus.IN_PROGRESS,
                        "   "));

        verifyNoInteractions(taskRepository, auditRepository);
    }

    @Test
    void shouldRejectReasonLongerThan500Characters() {

        String reason = "A".repeat(501);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.execute(
                        taskId,
                        adminUserId,
                        TaskStatus.IN_PROGRESS,
                        reason));

        verifyNoInteractions(taskRepository, auditRepository);
    }

    @Test
    void shouldRejectNullAdminUserId() {

        assertThrows(
                NullPointerException.class,
                () -> service.execute(
                        taskId,
                        null,
                        TaskStatus.IN_PROGRESS,
                        "Intervención justificada."));

        verifyNoInteractions(taskRepository, auditRepository);
    }

    @Test
    void shouldRejectNullNewStatus() {

        assertThrows(
                NullPointerException.class,
                () -> service.execute(
                        taskId,
                        adminUserId,
                        null,
                        "Intervención justificada."));

        verifyNoInteractions(taskRepository, auditRepository);
    }
}