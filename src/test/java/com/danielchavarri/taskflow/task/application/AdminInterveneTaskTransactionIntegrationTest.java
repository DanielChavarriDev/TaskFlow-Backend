
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;
import com.danielchavarri.taskflow.task.domain.port.TaskStatusAuditRepository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AdminInterveneTaskTransactionIntegrationTest {

    @Autowired
    private AdminInterveneTaskService adminInterveneTaskService;

    @Autowired
    private ApproveTaskCompletionService approveTaskCompletionService;

    @Autowired
    private RejectTaskCompletionService rejectTaskCompletionService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private TaskStatusAuditRepository auditRepository;

    private UUID userId;
    private UUID projectId;
    private UUID taskId;
    private UUID adminUserId;

    @BeforeEach
    void setUp() {

        userId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();

        Timestamp now = Timestamp.from(Instant.now());

        jdbcTemplate.update("""
                INSERT INTO users (
                    id,
                    name,
                    email,
                    password_hash,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?)
                """,
                userId,
                "Integration Test User",
                "integration-" + userId + "@taskflow.test",
                "test-password-hash",
                now);

        jdbcTemplate.update("""
                INSERT INTO projects (
                    id,
                    user_id,
                    name,
                    description,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?)
                """,
                projectId,
                userId,
                "Integration Test Project",
                "Proyecto para prueba transaccional",
                now);

        jdbcTemplate.update("""
                INSERT INTO tasks (
                    id,
                    project_id,
                    title,
                    description,
                    status,
                    priority,
                    due_date,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """,
                taskId,
                projectId,
                "Integration Test Task",
                "Tarea para comprobar rollback",
                "IN_REVIEW",
                "MEDIUM",
                null,
                now);
    }

    @AfterEach
    void tearDown() {

        jdbcTemplate.update(
                "DELETE FROM projects WHERE id = ?",
                projectId);

        jdbcTemplate.update(
                "DELETE FROM users WHERE id = ?",
                userId);
    }

    @Test
    void shouldRollbackTaskStatusWhenAuditPersistenceFails() {

        doThrow(new IllegalStateException(
                "Simulated audit persistence failure"))
                .when(auditRepository)
                .save(any(TaskStatusAudit.class));

        assertThrows(
                IllegalStateException.class,
                () -> adminInterveneTaskService.execute(
                        taskId,
                        adminUserId,
                        com.danielchavarri.taskflow.task.domain.model.TaskStatus.COMPLETED,
                        "Intervención administrativa de prueba"));

        String persistedStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM tasks WHERE id = ?",
                String.class,
                taskId);

        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM task_status_audit WHERE task_id = ?",
                Integer.class,
                taskId);

        assertEquals(
                "IN_REVIEW",
                persistedStatus,
                "El estado de la tarea debe revertirse.");

        assertEquals(
                0,
                auditCount,
                "No debe existir una auditoría parcial.");

        verify(auditRepository).save(any(TaskStatusAudit.class));
    }

    @Test
    void shouldRollbackTaskStatusWhenApprovalAuditPersistenceFails() {

        doThrow(new IllegalStateException(
                "Simulated audit persistence failure"))
                .when(auditRepository)
                .save(any(TaskStatusAudit.class));

        assertThrows(
                IllegalStateException.class,
                () -> approveTaskCompletionService.execute(
                        taskId,
                        adminUserId,
                        "Aprobación administrativa de prueba"));

        String persistedStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM tasks WHERE id = ?",
                String.class,
                taskId);

        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM task_status_audit WHERE task_id = ?",
                Integer.class,
                taskId);

        assertEquals(
                "IN_REVIEW",
                persistedStatus,
                "El estado debe revertirse cuando falla la auditoría de aprobación.");

        assertEquals(
                0,
                auditCount,
                "No debe existir una auditoría parcial.");

        verify(auditRepository).save(any(TaskStatusAudit.class));
    }

    @Test
    void shouldRollbackTaskStatusWhenRejectionAuditPersistenceFails() {

        doThrow(new IllegalStateException(
                "Simulated audit persistence failure"))
                .when(auditRepository)
                .save(any(TaskStatusAudit.class));

        assertThrows(
                IllegalStateException.class,
                () -> rejectTaskCompletionService.execute(
                        taskId,
                        adminUserId,
                        "Rechazo administrativo de prueba"));

        String persistedStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM tasks WHERE id = ?",
                String.class,
                taskId);

        Integer auditCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM task_status_audit WHERE task_id = ?",
                Integer.class,
                taskId);

        assertEquals(
                "IN_REVIEW",
                persistedStatus,
                "El estado debe revertirse cuando falla la auditoría de rechazo.");

        assertEquals(
                0,
                auditCount,
                "No debe existir una auditoría parcial.");

        verify(auditRepository).save(any(TaskStatusAudit.class));
    }
}