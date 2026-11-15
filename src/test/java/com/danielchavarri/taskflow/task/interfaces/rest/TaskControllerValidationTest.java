
package com.danielchavarri.taskflow.task.interfaces.rest;

import com.danielchavarri.taskflow.common.interfaces.rest.GlobalExceptionHandler;
import com.danielchavarri.taskflow.shared.infrastructure.security.JwtService;
import com.danielchavarri.taskflow.task.application.AdminInterveneTaskService;
import com.danielchavarri.taskflow.task.application.ApproveTaskCompletionService;
import com.danielchavarri.taskflow.task.application.CreateTaskService;
import com.danielchavarri.taskflow.task.application.DeleteTaskService;
import com.danielchavarri.taskflow.task.application.GetFilteredTasksService;
import com.danielchavarri.taskflow.task.application.GetTaskByIdService;
import com.danielchavarri.taskflow.task.application.GetTaskStatusAuditService;
import com.danielchavarri.taskflow.task.application.GetTasksByProjectService;
import com.danielchavarri.taskflow.task.application.GetTasksPendingReviewService;
import com.danielchavarri.taskflow.task.application.RejectTaskCompletionService;
import com.danielchavarri.taskflow.task.application.RequestTaskCompletionService;
import com.danielchavarri.taskflow.task.application.StartTaskService;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import com.danielchavarri.taskflow.task.application.UpdateTaskService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verifyNoInteractions;

@WebMvcTest(TaskController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class TaskControllerValidationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private CreateTaskService createTaskService;

        @MockitoBean
        private GetFilteredTasksService getFilteredTasksService;

        @MockitoBean
        private GetTaskByIdService getTaskByIdService;

        @MockitoBean
        private GetTasksByProjectService getTasksByProjectService;

        @MockitoBean
        private UpdateTaskService updateTaskService;

        @MockitoBean
        private DeleteTaskService deleteTaskService;

        @MockitoBean
        private StartTaskService startTaskService;

        @MockitoBean
        private RequestTaskCompletionService requestTaskCompletionService;

        @MockitoBean
        private ApproveTaskCompletionService approveTaskCompletionService;

        @MockitoBean
        private RejectTaskCompletionService rejectTaskCompletionService;

        @MockitoBean
        private AdminInterveneTaskService adminInterveneTaskService;

        @MockitoBean
        private GetTaskStatusAuditService getTaskStatusAuditService;

        @MockitoBean
        private GetTasksPendingReviewService getTasksPendingReviewService;

        @MockitoBean
        private UserRepository userRepository;

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
        void shouldReturn400WhenTitleExceeds150Characters() throws Exception {
                String longTitle = "A".repeat(151);

                String requestBody = """
                                {
                                    "projectId": "00000000-0000-0000-0000-000000000001",
                                    "title": "%s",
                                    "description": "Tarea de prueba",
                                    "priority": "HIGH",
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
        void shouldReturn400WhenDescriptionExceeds1000Characters() throws Exception {
                String longDescription = "A".repeat(1001);

                String requestBody = """
                                {
                                    "projectId": "00000000-0000-0000-0000-000000000001",
                                    "title": "Tarea de prueba",
                                    "description": "%s",
                                    "priority": "HIGH",
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
}