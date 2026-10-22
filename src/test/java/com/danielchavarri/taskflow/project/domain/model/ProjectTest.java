
package com.danielchavarri.taskflow.project.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void createProjectWithValidData() {
        Project project = Project.create(
                "TaskFlow",
                "Gestión de proyectos y tareas");

        assertNotNull(project);
        assertEquals("TaskFlow", project.getName());
        assertEquals(
                "Gestión de proyectos y tareas",
                project.getDescription());
        assertNotNull(project.getId());
        assertNotNull(project.getCreatedAt());
    }
}