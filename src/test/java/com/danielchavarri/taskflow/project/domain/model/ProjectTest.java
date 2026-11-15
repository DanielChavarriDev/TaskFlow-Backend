
package com.danielchavarri.taskflow.project.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void shouldCreateProjectWithValidData() {
        UUID ownerId = UUID.randomUUID();

        Project project = Project.create(
                ownerId,
                "TaskFlow",
                "Gestión de proyectos y tareas");

        assertNotNull(project);
        assertNotNull(project.getId());
        assertEquals(ownerId, project.getOwnerId());
        assertEquals("TaskFlow", project.getName());
        assertEquals(
                "Gestión de proyectos y tareas",
                project.getDescription());
        assertNotNull(project.getCreatedAt());
    }

    @Test
    void shouldTrimProjectNameAndDescription() {
        UUID ownerId = UUID.randomUUID();

        Project project = Project.create(
                ownerId,
                "  TaskFlow  ",
                "  Gestión de proyectos  ");

        assertEquals("TaskFlow", project.getName());
        assertEquals("Gestión de proyectos", project.getDescription());
    }

    @Test
    void shouldNormalizeBlankDescriptionToNull() {
        UUID ownerId = UUID.randomUUID();

        Project project = Project.create(
                ownerId,
                "TaskFlow",
                "   ");

        assertNull(project.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenNameIsNull() {
        UUID ownerId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> Project.create(ownerId, null, "Descripción"));
    }

    @Test
    void shouldThrowExceptionWhenNameIsBlank() {
        UUID ownerId = UUID.randomUUID();

        assertThrows(
                IllegalArgumentException.class,
                () -> Project.create(ownerId, "   ", "Descripción"));
    }

    @Test
    void shouldThrowExceptionWhenNameExceeds100Characters() {
        UUID ownerId = UUID.randomUUID();
        String longName = "A".repeat(101);

        assertThrows(
                IllegalArgumentException.class,
                () -> Project.create(ownerId, longName, "Descripción"));
    }

    @Test
    void shouldThrowExceptionWhenDescriptionExceeds500Characters() {
        UUID ownerId = UUID.randomUUID();
        String longDescription = "A".repeat(501);

        assertThrows(
                IllegalArgumentException.class,
                () -> Project.create(ownerId, "TaskFlow", longDescription));
    }

    @Test
    void shouldThrowExceptionWhenOwnerIdIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> Project.create(null, "TaskFlow", "Descripción"));
    }

    @Test
    void shouldUpdateProjectDetails() {
        UUID ownerId = UUID.randomUUID();

        Project project = Project.create(
                ownerId,
                "TaskFlow",
                "Descripción inicial");

        project.updateDetails(
                "TaskFlow actualizado",
                "Nueva descripción");

        assertEquals("TaskFlow actualizado", project.getName());
        assertEquals("Nueva descripción", project.getDescription());
    }

    @Test
    void shouldPreserveOwnerIdWhenUpdatingProject() {
        UUID ownerId = UUID.randomUUID();

        Project project = Project.create(
                ownerId,
                "TaskFlow",
                "Descripción inicial");

        project.updateDetails(
                "TaskFlow actualizado",
                "Nueva descripción");

        assertEquals(ownerId, project.getOwnerId());
    }
}