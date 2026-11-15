
package com.danielchavarri.taskflow.task.application;

import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import com.danielchavarri.taskflow.task.domain.model.TaskStatusAudit;
import com.danielchavarri.taskflow.task.interfaces.rest.TaskStatusAuditResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskStatusAuditPersistenceIntegrationTest {

    @Autowired
    private ApproveTaskCompletionService approveTaskCompletionService;

    @Autowired
    private GetTaskStatusAuditService getTaskStatusAuditService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RejectTaskCompletionService rejectTaskCompletionService;

    private UUID ownerId;
    private UUID adminUserId;
    private UUID projectId;
    private UUID taskId;

    @BeforeEach
    void setUp() {

        ownerId = UUID.randomUUID();
        adminUserId = UUID.randomUUID();
        projectId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        Timestamp now = Timestamp.from(Instant.now());

        jdbcTemplate.update("""
                INSERT INTO users (
                    id, name, email, password_hash, created_at
                )
                VALUES (?, ?, ?, ?, ?)
                """,
                ownerId,
                "Audit Test Owner",
                "owner-" + ownerId + "@taskflow.test",
                "test-password-hash",
                now);

        jdbcTemplate.update("""
                INSERT INTO users (
                    id, name, email, password_hash, created_at
                )
                VALUES (?, ?, ?, ?, ?)
                """,
                adminUserId,
                "Audit Test Admin",
                "admin-" + adminUserId + "@taskflow.test",
                "test-password-hash",
                now);

        jdbcTemplate.update("""
                INSERT INTO projects (
                    id, user_id, name, description, created_at
                )
                VALUES (?, ?, ?, ?, ?)
                """,
                projectId,
                ownerId,
                "Audit Integration Project",
                "Proyecto para probar persistencia de auditorías",
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
                "Audit Integration Task",
                "Tarea para comprobar persistencia real",
                "IN_REVIEW",
                "HIGH",
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
                adminUserId);

        jdbcTemplate.update(
                "DELETE FROM users WHERE id = ?",
                ownerId);
    }

    @Test
    void shouldPersistAndRetrieveAuditWithRealJpaRepository() {

        String reason = "  Se verificó el cumplimiento de los criterios.  ";

        // Ejecutar el servicio real, sin simular el repositorio de auditoría.
        var approvedTask = approveTaskCompletionService.execute(
                taskId,
                adminUserId,
                reason);

        assertEquals(TaskStatus.COMPLETED, approvedTask.getStatus());

        // Comprobar el estado persistido en PostgreSQL.
        String persistedStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM tasks WHERE id = ?",
                String.class,
                taskId);

        assertEquals("COMPLETED", persistedStatus);

        // Consultar directamente el registro de auditoría en PostgreSQL.
        var auditRow = jdbcTemplate.queryForMap("""
                SELECT
                    id,
                    task_id,
                    admin_user_id,
                    previous_status,
                    new_status,
                    reason,
                    created_at
                FROM task_status_audit
                WHERE task_id = ?
                """,
                taskId);

        assertEquals(taskId, auditRow.get("task_id"));
        assertEquals(adminUserId, auditRow.get("admin_user_id"));
        assertEquals("IN_REVIEW", auditRow.get("previous_status"));
        assertEquals("COMPLETED", auditRow.get("new_status"));
        assertEquals(
                "Se verificó el cumplimiento de los criterios.",
                auditRow.get("reason"));
        assertNotNull(auditRow.get("id"));
        assertNotNull(auditRow.get("created_at"));

        // Consultar mediante el servicio real y el adaptador JPA.
        List<TaskStatusAudit> audits = getTaskStatusAuditService.execute(taskId);

        assertEquals(1, audits.size());

        TaskStatusAudit audit = audits.get(0);

        assertEquals(taskId, audit.taskId());
        assertEquals(adminUserId, audit.adminUserId());
        assertEquals(TaskStatus.IN_REVIEW, audit.previousStatus());
        assertEquals(TaskStatus.COMPLETED, audit.newStatus());
        assertEquals(
                "Se verificó el cumplimiento de los criterios.",
                audit.reason());
        assertNotNull(audit.createdAt());

        // Comprobar la conversión al formato de respuesta REST.
        TaskStatusAuditResponse response = TaskStatusAuditResponse.from(audit);

        assertEquals(taskId, response.taskId());
        assertEquals(adminUserId, response.adminUserId());
        assertEquals("IN_REVIEW", response.previousStatus());
        assertEquals("COMPLETED", response.newStatus());
        assertEquals(
                "Se verificó el cumplimiento de los criterios.",
                response.reason());
        assertNotNull(response.id());
        assertNotNull(response.createdAt());
    }

    @Test
    void shouldPersistAndRetrieveRejectionAudit() {

        String reason = "  La evidencia presentada es insuficiente.  ";

        // Ejecutar el servicio real de rechazo.
        var rejectedTask = rejectTaskCompletionService.execute(
                taskId,
                adminUserId,
                reason);

        assertEquals(TaskStatus.IN_PROGRESS, rejectedTask.getStatus());

        // Comprobar el estado persistido en PostgreSQL.
        String persistedStatus = jdbcTemplate.queryForObject(
                "SELECT status FROM tasks WHERE id = ?",
                String.class,
                taskId);

        assertEquals("IN_PROGRESS", persistedStatus);

        // Comprobar la auditoría persistida.
        var auditRow = jdbcTemplate.queryForMap("""
                SELECT
                    id,
                    task_id,
                    admin_user_id,
                    previous_status,
                    new_status,
                    reason,
                    created_at
                FROM task_status_audit
                WHERE task_id = ?
                """,
                taskId);

        assertEquals(taskId, auditRow.get("task_id"));
        assertEquals(adminUserId, auditRow.get("admin_user_id"));
        assertEquals("IN_REVIEW", auditRow.get("previous_status"));
        assertEquals("IN_PROGRESS", auditRow.get("new_status"));
        assertEquals(
                "La evidencia presentada es insuficiente.",
                auditRow.get("reason"));

        assertNotNull(auditRow.get("id"));
        assertNotNull(auditRow.get("created_at"));

        // Recuperar la auditoría mediante el servicio real.
        List<TaskStatusAudit> audits = getTaskStatusAuditService.execute(taskId);

        assertEquals(1, audits.size());

        TaskStatusAudit audit = audits.get(0);

        assertEquals(taskId, audit.taskId());
        assertEquals(adminUserId, audit.adminUserId());
        assertEquals(TaskStatus.IN_REVIEW, audit.previousStatus());
        assertEquals(TaskStatus.IN_PROGRESS, audit.newStatus());
        assertEquals(
                "La evidencia presentada es insuficiente.",
                audit.reason());

        // Comprobar la conversión a la respuesta REST.
        TaskStatusAuditResponse response = TaskStatusAuditResponse.from(audit);

        assertEquals(taskId, response.taskId());
        assertEquals(adminUserId, response.adminUserId());
        assertEquals("IN_REVIEW", response.previousStatus());
        assertEquals("IN_PROGRESS", response.newStatus());
        assertEquals(
                "La evidencia presentada es insuficiente.",
                response.reason());

        assertNotNull(response.id());
        assertNotNull(response.createdAt());
    }

    @Test
    void shouldReturnAuditsOrderedByCreatedAtDescending() {

        Instant olderDate = Instant.parse("2026-11-14T10:00:00Z");
        Instant newerDate = Instant.parse("2026-11-14T12:00:00Z");

        UUID olderAuditId = UUID.randomUUID();
        UUID newerAuditId = UUID.randomUUID();

        // Insertar una auditoría antigua.
        jdbcTemplate.update("""
                INSERT INTO task_status_audit (
                    id,
                    task_id,
                    admin_user_id,
                    previous_status,
                    new_status,
                    reason,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                olderAuditId,
                taskId,
                adminUserId,
                "IN_REVIEW",
                "IN_PROGRESS",
                "Primera intervención administrativa.",
                Timestamp.from(olderDate));

        // Insertar una auditoría más reciente.
        jdbcTemplate.update("""
                INSERT INTO task_status_audit (
                    id,
                    task_id,
                    admin_user_id,
                    previous_status,
                    new_status,
                    reason,
                    created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                newerAuditId,
                taskId,
                adminUserId,
                "IN_PROGRESS",
                "IN_REVIEW",
                "Segunda intervención administrativa.",
                Timestamp.from(newerDate));

        // Consultar mediante el servicio y repositorio reales.
        List<TaskStatusAudit> audits = getTaskStatusAuditService.execute(taskId);

        assertEquals(2, audits.size());

        // Verificar que el registro más reciente aparezca primero.
        assertEquals(newerAuditId, audits.get(0).id());
        assertEquals(olderAuditId, audits.get(1).id());

        assertTrue(
                audits.get(0).createdAt().isAfter(audits.get(1).createdAt()));
    }
}