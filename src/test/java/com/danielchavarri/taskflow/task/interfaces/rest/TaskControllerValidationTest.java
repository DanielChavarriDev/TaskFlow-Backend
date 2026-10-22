
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.common.interfaces.rest.GlobalExceptionHandler;
import com.danielchavarri.taskflow.task.application.ChangeTaskStatusService;
import com.danielchavarri.taskflow.task.application.CreateTaskService;
import com.danielchavarri.taskflow.task.application.DeleteTaskService;
import com.danielchavarri.taskflow.task.application.GetAllTasksService;
import com.danielchavarri.taskflow.task.application.GetTaskByIdService;
import com.danielchavarri.taskflow.task.application.GetTasksByProjectService;
import com.danielchavarri.taskflow.task.application.UpdateTaskService;
import org.junit.jupiter.api.Test;
import com.danielchavarri.taskflow.task.application.TaskNotFoundException;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(GlobalExceptionHandler.class)
class TaskControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateTaskService createTaskService;

    @MockitoBean
    private GetAllTasksService getAllTasksService;

    @MockitoBean
    private GetTaskByIdService getTaskByIdService;

    @MockitoBean
    private GetTasksByProjectService getTasksByProjectService;

    @MockitoBean
    private UpdateTaskService updateTaskService;

    @MockitoBean
    private ChangeTaskStatusService changeTaskStatusService;

    @MockitoBean
    private DeleteTaskService deleteTaskService;

    @Test
    void shouldReturn400WhenRequiredFieldsAreMissing() throws Exception {
        String requestBody = """
                {
                    "projectId": null,
                    "title": "",
                    "description": "Tarea de prueba",
                    "priority": null,
                    "dueDate": null
                }
                """;

        mockMvc.perform(post("/api/tasks")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.projectId")
                        .value("El proyecto es obligatorio."))
                .andExpect(jsonPath("$.errors.title")
                        .value("El título es obligatorio."))
                .andExpect(jsonPath("$.errors.priority")
                        .value("La prioridad es obligatoria."));

        verifyNoInteractions(createTaskService);
    }

    @Test
    void shouldReturn400WhenTitleExceeds150Characters()
            throws Exception {
        String longTitle = "A".repeat(151);

        String requestBody = """
                {
                    "projectId": null,
                    "title": "%s",
                    "description": "Tarea de prueba",
                    "priority": null,
                    "dueDate": null
                }
                """.formatted(longTitle);

        mockMvc.perform(post("/api/tasks")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.title")
                        .value("El título no puede superar los 150 caracteres."));

        verifyNoInteractions(createTaskService);
    }

    @Test
    void shouldReturn400WhenDescriptionExceeds1000Characters()
            throws Exception {
        String longDescription = "A".repeat(1001);

        String requestBody = """
                {
                    "projectId": null,
                    "title": "Tarea de prueba",
                    "description": "%s",
                    "priority": null,
                    "dueDate": null
                }
                """.formatted(longDescription);

        mockMvc.perform(post("/api/tasks")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors.description")
                        .value("La descripción no puede superar los 1000 caracteres."));

        verifyNoInteractions(createTaskService);
    }

    @Test
    void shouldReturn400WhenPriorityIsMissing() throws Exception {
        String requestBody = """
                {
                    "projectId": "00000000-0000-0000-0000-000000000001",
                    "title": "Tarea de prueba",
                    "description": "Descripción válida",
                    "priority": null,
                    "dueDate": null
                }
                """;

        mockMvc.perform(post("/api/tasks")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors.priority")
                        .value("La prioridad es obligatoria."));

        verifyNoInteractions(createTaskService);
    }

    @Test
    void shouldReturn400WhenRequestBodyContainsMalformedJson() throws Exception {
        String malformedJson = """
                {
                    "projectId": "00000000-0000-0000-0000-000000000001",
                    "title": "Tarea de prueba",
                    "priority":
                }
                """;

        mockMvc.perform(post("/api/tasks")
                .contentType(APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Malformed request"));

        verifyNoInteractions(createTaskService);
    }

    @Test
    void shouldReturn400WhenTaskIdIsNotAValidUuid() throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", "no-es-un-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.title").value("Invalid parameter"));

        verifyNoInteractions(getTaskByIdService);
    }

    @Test
    void shouldReturn404WhenTaskDoesNotExist() throws Exception {
        java.util.UUID taskId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000000");

        when(getTaskByIdService.execute(taskId))
                .thenThrow(new TaskNotFoundException(taskId));

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").value("Task not found"))
                .andExpect(jsonPath("$.detail")
                        .value("No se encontró la tarea con ID: " + taskId));
    }

    @Test
    void shouldReturn201WhenTaskIsCreatedSuccessfully() throws Exception {
        java.util.UUID projectId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000001");

        java.time.LocalDate dueDate = java.time.LocalDate.parse("2026-11-15");

        com.danielchavarri.taskflow.task.domain.model.Task task = com.danielchavarri.taskflow.task.domain.model.Task
                .create(
                        projectId,
                        "Implementar autenticación",
                        "Crear el endpoint de inicio de sesión",
                        com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                        dueDate);

        when(createTaskService.execute(
                projectId,
                "Implementar autenticación",
                "Crear el endpoint de inicio de sesión",
                com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                dueDate))
                .thenReturn(task);

        String requestBody = """
                {
                    "projectId": "%s",
                    "title": "Implementar autenticación",
                    "description": "Crear el endpoint de inicio de sesión",
                    "priority": "HIGH",
                    "dueDate": "2026-11-15"
                }
                """.formatted(projectId);

        mockMvc.perform(post("/api/tasks")
                .contentType(APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(task.getId().toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.title").value("Implementar autenticación"))
                .andExpect(jsonPath("$.description")
                        .value("Crear el endpoint de inicio de sesión"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").value("2026-11-15"))
                .andExpect(jsonPath("$.createdAt").exists());

        org.mockito.Mockito.verify(createTaskService).execute(
                projectId,
                "Implementar autenticación",
                "Crear el endpoint de inicio de sesión",
                com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                dueDate);
    }

    @Test
    void shouldReturn200WhenTaskIsRetrievedSuccessfully() throws Exception {
        java.util.UUID taskId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000010");
        java.util.UUID projectId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000001");

        java.time.Instant createdAt = java.time.Instant.parse("2026-10-22T12:00:00Z");

        com.danielchavarri.taskflow.task.domain.model.Task task = com.danielchavarri.taskflow.task.domain.model.Task
                .reconstitute(
                        taskId,
                        projectId,
                        "Implementar autenticación",
                        "Crear el endpoint de inicio de sesión",
                        com.danielchavarri.taskflow.task.domain.model.TaskStatus.PENDING,
                        com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                        java.time.LocalDate.parse("2026-11-15"),
                        createdAt);

        when(getTaskByIdService.execute(taskId)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.title").value("Implementar autenticación"))
                .andExpect(jsonPath("$.description")
                        .value("Crear el endpoint de inicio de sesión"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").value("2026-11-15"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        org.mockito.Mockito.verify(getTaskByIdService).execute(taskId);
    }

    @Test
    void shouldReturn200WhenTasksAreRetrievedSuccessfully() throws Exception {
        java.util.UUID projectId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000001");

        com.danielchavarri.taskflow.task.domain.model.Task task = com.danielchavarri.taskflow.task.domain.model.Task
                .create(
                        projectId,
                        "Implementar autenticación",
                        "Crear el endpoint de inicio de sesión",
                        com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                        java.time.LocalDate.parse("2026-11-15"));

        when(getAllTasksService.execute())
                .thenReturn(java.util.List.of(task));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(task.getId().toString()))
                .andExpect(jsonPath("$[0].projectId").value(projectId.toString()))
                .andExpect(jsonPath("$[0].title")
                        .value("Implementar autenticación"))
                .andExpect(jsonPath("$[0].description")
                        .value("Crear el endpoint de inicio de sesión"))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"))
                .andExpect(jsonPath("$[0].dueDate").value("2026-11-15"));

        org.mockito.Mockito.verify(getAllTasksService).execute();
    }

    @Test
    void shouldReturn200WhenTasksAreRetrievedByProjectSuccessfully()
            throws Exception {
        java.util.UUID projectId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000001");

        com.danielchavarri.taskflow.task.domain.model.Task task = com.danielchavarri.taskflow.task.domain.model.Task
                .create(
                        projectId,
                        "Implementar autenticación",
                        "Crear el endpoint de inicio de sesión",
                        com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                        java.time.LocalDate.parse("2026-11-15"));

        when(getTasksByProjectService.execute(projectId))
                .thenReturn(java.util.List.of(task));

        mockMvc.perform(get("/api/tasks/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(task.getId().toString()))
                .andExpect(jsonPath("$[0].projectId").value(projectId.toString()))
                .andExpect(jsonPath("$[0].title")
                        .value("Implementar autenticación"))
                .andExpect(jsonPath("$[0].priority").value("HIGH"));

        org.mockito.Mockito.verify(getTasksByProjectService)
                .execute(projectId);
    }

    @Test
    void shouldReturn200WhenTaskIsUpdatedSuccessfully() throws Exception {
        java.util.UUID taskId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000010");
        java.util.UUID projectId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000001");

        java.time.Instant createdAt = java.time.Instant.parse("2026-10-22T12:00:00Z");

        java.time.LocalDate dueDate = java.time.LocalDate.parse("2026-11-20");

        com.danielchavarri.taskflow.task.domain.model.Task task = com.danielchavarri.taskflow.task.domain.model.Task
                .reconstitute(
                        taskId,
                        projectId,
                        "Tarea actualizada",
                        "Descripción actualizada",
                        com.danielchavarri.taskflow.task.domain.model.TaskStatus.PENDING,
                        com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                        dueDate,
                        createdAt);

        when(updateTaskService.execute(
                taskId,
                "Tarea actualizada",
                "Descripción actualizada",
                com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                dueDate))
                .thenReturn(task);

        String requestBody = """
                {
                    "title": "Tarea actualizada",
                    "description": "Descripción actualizada",
                    "priority": "HIGH",
                    "dueDate": "2026-11-20"
                }
                """;

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                .put("/api/tasks/{id}", taskId)
                .contentType(APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.projectId").value(projectId.toString()))
                .andExpect(jsonPath("$.title").value("Tarea actualizada"))
                .andExpect(jsonPath("$.description")
                        .value("Descripción actualizada"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.dueDate").value("2026-11-20"))
                .andExpect(jsonPath("$.createdAt").value(createdAt.toString()));

        org.mockito.Mockito.verify(updateTaskService).execute(
                taskId,
                "Tarea actualizada",
                "Descripción actualizada",
                com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                dueDate);
    }

    @Test
    void shouldReturn200WhenTaskStatusIsChangedSuccessfully() throws Exception {
        java.util.UUID taskId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000010");
        java.util.UUID projectId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000001");

        java.time.Instant createdAt = java.time.Instant.parse("2026-10-22T12:00:00Z");

        com.danielchavarri.taskflow.task.domain.model.Task task = com.danielchavarri.taskflow.task.domain.model.Task
                .reconstitute(
                        taskId,
                        projectId,
                        "Implementar autenticación",
                        "Crear el endpoint de inicio de sesión",
                        com.danielchavarri.taskflow.task.domain.model.TaskStatus.IN_PROGRESS,
                        com.danielchavarri.taskflow.task.domain.model.TaskPriority.HIGH,
                        java.time.LocalDate.parse("2026-11-15"),
                        createdAt);

        when(changeTaskStatusService.execute(
                taskId,
                com.danielchavarri.taskflow.task.domain.model.TaskStatus.IN_PROGRESS))
                .thenReturn(task);

        String requestBody = """
                {
                    "status": "IN_PROGRESS"
                }
                """;

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .patch("/api/tasks/{id}/status", taskId)
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.title")
                        .value("Implementar autenticación"))
                .andExpect(jsonPath("$.priority").value("HIGH"));

        org.mockito.Mockito.verify(changeTaskStatusService).execute(
                taskId,
                com.danielchavarri.taskflow.task.domain.model.TaskStatus.IN_PROGRESS);
    }

    @Test
    void shouldReturn204WhenTaskIsDeletedSuccessfully() throws Exception {
        java.util.UUID taskId = java.util.UUID.fromString(
                "00000000-0000-0000-0000-000000000010");

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .delete("/api/tasks/{id}", taskId))
                .andExpect(status().isNoContent())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers
                        .content().string(""));

        org.mockito.Mockito.verify(deleteTaskService).execute(taskId);
    }
}