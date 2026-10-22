
package com.danielchavarri.taskflow.task.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private final UUID projectId = UUID.randomUUID();

    @Test
    void shouldCreateTaskWithPendingStatus() {
        Task task = Task.create(
                projectId,
                "Implementar módulo de tareas",
                "Crear las operaciones CRUD",
                TaskPriority.HIGH,
                LocalDate.of(2026, 10, 30));

        assertNotNull(task.getId());
        assertEquals(projectId, task.getProjectId());
        assertEquals("Implementar módulo de tareas", task.getTitle());
        assertEquals("Crear las operaciones CRUD", task.getDescription());
        assertEquals(TaskStatus.PENDING, task.getStatus());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(LocalDate.of(2026, 10, 30), task.getDueDate());
        assertNotNull(task.getCreatedAt());
    }

    @Test
    void shouldTrimTitleAndDescription() {
        Task task = Task.create(
                projectId,
                "  Estudiar Java  ",
                "  Practicar pruebas unitarias  ",
                TaskPriority.MEDIUM,
                null);

        assertEquals("Estudiar Java", task.getTitle());
        assertEquals(
                "Practicar pruebas unitarias",
                task.getDescription());
    }

    @Test
    void shouldRejectBlankTitle() {
        assertThrows(IllegalArgumentException.class, () -> Task.create(
                projectId,
                "   ",
                "Descripción válida",
                TaskPriority.LOW,
                null));
    }

    @Test
    void shouldRejectTitleLongerThan150Characters() {
        String title = "A".repeat(151);

        assertThrows(IllegalArgumentException.class, () -> Task.create(
                projectId,
                title,
                null,
                TaskPriority.LOW,
                null));
    }

    @Test
    void shouldRejectDescriptionLongerThan1000Characters() {
        String description = "A".repeat(1001);

        assertThrows(IllegalArgumentException.class, () -> Task.create(
                projectId,
                "Tarea válida",
                description,
                TaskPriority.MEDIUM,
                null));
    }

    @Test
    void shouldUpdateTaskDetails() {
        Task task = Task.create(
                projectId,
                "Título anterior",
                "Descripción anterior",
                TaskPriority.LOW,
                null);

        task.updateDetails(
                "Título nuevo",
                "Descripción nueva",
                TaskPriority.HIGH,
                LocalDate.of(2026, 11, 15));

        assertEquals("Título nuevo", task.getTitle());
        assertEquals("Descripción nueva", task.getDescription());
        assertEquals(TaskPriority.HIGH, task.getPriority());
        assertEquals(
                LocalDate.of(2026, 11, 15),
                task.getDueDate());
    }

    @Test
    void shouldChangeTaskStatus() {
        Task task = Task.create(
                projectId,
                "Completar pruebas",
                null,
                TaskPriority.HIGH,
                null);

        task.changeStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());

        task.changeStatus(TaskStatus.COMPLETED);

        assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void shouldReconstituteTaskWithExistingValues() {
        UUID taskId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-10-20T10:00:00Z");

        Task task = Task.reconstitute(
                taskId,
                projectId,
                "Tarea recuperada",
                "Descripción recuperada",
                TaskStatus.COMPLETED,
                TaskPriority.MEDIUM,
                LocalDate.of(2026, 10, 25),
                createdAt);

        assertEquals(taskId, task.getId());
        assertEquals(projectId, task.getProjectId());
        assertEquals("Tarea recuperada", task.getTitle());
        assertEquals(TaskStatus.COMPLETED, task.getStatus());
        assertEquals(createdAt, task.getCreatedAt());
    }
}