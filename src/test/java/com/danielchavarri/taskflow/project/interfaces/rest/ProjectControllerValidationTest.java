
package com.danielchavarri.taskflow.project.interfaces.rest;

import com.danielchavarri.taskflow.common.interfaces.rest.GlobalExceptionHandler;
import com.danielchavarri.taskflow.project.application.CreateProjectUseCase;
import com.danielchavarri.taskflow.project.application.DeleteProjectUseCase;
import com.danielchavarri.taskflow.project.application.GetAllProjectsUseCase;
import com.danielchavarri.taskflow.project.application.GetProjectByIdUseCase;
import com.danielchavarri.taskflow.project.application.UpdateProjectUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.danielchavarri.taskflow.project.application.ProjectNotFoundException;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import com.danielchavarri.taskflow.project.domain.model.Project;
import static org.mockito.ArgumentMatchers.anyString;
import com.danielchavarri.taskflow.shared.infrastructure.security.JwtService;
import static org.mockito.Mockito.verify;
import org.springframework.dao.DataIntegrityViolationException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@Import(GlobalExceptionHandler.class)
class ProjectControllerValidationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private CreateProjectUseCase createProjectUseCase;

        @MockitoBean
        private GetAllProjectsUseCase getAllProjectsUseCase;

        @MockitoBean
        private GetProjectByIdUseCase getProjectByIdUseCase;

        @MockitoBean
        private UpdateProjectUseCase updateProjectUseCase;

        @MockitoBean
        private DeleteProjectUseCase deleteProjectUseCase;

        @Test
        void shouldReturn400WhenProjectNameIsBlank() throws Exception {
                String requestBody = """
                                {
                                    "name": "",
                                    "description": "Proyecto de prueba"
                                }
                                """;

                mockMvc.perform(post("/api/projects")
                                .contentType("application/json")
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.title").value("Validation failed"))
                                .andExpect(jsonPath("$.errors.name")
                                                .value("El nombre es obligatorio"));

                verifyNoInteractions(createProjectUseCase);
        }

        @Test
        void shouldReturn400WhenProjectNameExceeds100Characters()
                        throws Exception {
                String longName = "A".repeat(101);

                String requestBody = """
                                {
                                    "name": "%s",
                                    "description": "Proyecto de prueba"
                                }
                                """.formatted(longName);

                mockMvc.perform(post("/api/projects")
                                .contentType("application/json")
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.title").value("Validation failed"))
                                .andExpect(jsonPath("$.errors.name")
                                                .value("Máximo 100 caracteres"));

                verifyNoInteractions(createProjectUseCase);
        }

        @Test
        void shouldReturn400WhenDescriptionExceeds500Characters()
                        throws Exception {
                String longDescription = "A".repeat(501);

                String requestBody = """
                                {
                                    "name": "Proyecto de prueba",
                                    "description": "%s"
                                }
                                """.formatted(longDescription);

                mockMvc.perform(post("/api/projects")
                                .contentType("application/json")
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.title").value("Validation failed"))
                                .andExpect(jsonPath("$.errors.description")
                                                .value("Máximo 500 caracteres"));

                verifyNoInteractions(createProjectUseCase);
        }

        @Test
        void shouldReturn400WhenRequestBodyContainsMalformedJson() throws Exception {
                String malformedJson = """
                                {
                                    "name": "Proyecto de prueba",
                                    "description":
                                }
                                """;

                mockMvc.perform(post("/api/projects")
                                .contentType("application/json")
                                .content(malformedJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.title").value("Malformed request"));

                verifyNoInteractions(createProjectUseCase);
        }

        @Test
        void shouldReturn400WhenProjectIdIsNotAValidUuid() throws Exception {
                mockMvc.perform(get("/api/projects/{id}", "no-es-un-uuid"))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.title").value("Invalid parameter"));

                verifyNoInteractions(getProjectByIdUseCase);
        }

        @Test
        void shouldReturn404WhenProjectDoesNotExist() throws Exception {
                java.util.UUID projectId = java.util.UUID.fromString(
                                "00000000-0000-0000-0000-000000000000");

                when(getProjectByIdUseCase.execute(projectId))
                                .thenThrow(new ProjectNotFoundException(projectId));

                mockMvc.perform(get("/api/projects/{id}", projectId))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.title").value("Project not found"))
                                .andExpect(jsonPath("$.detail")
                                                .value("No se encontró el proyecto con ID: " + projectId));
        }

        @Test
        void shouldReturn409WhenProjectDataConflicts() throws Exception {
                when(createProjectUseCase.execute(anyString(), anyString()))
                                .thenThrow(new DataIntegrityViolationException(
                                                "Data integrity conflict"));

                String requestBody = """
                                {
                                    "name": "Proyecto de prueba",
                                    "description": "Descripción del proyecto"
                                }
                                """;

                mockMvc.perform(post("/api/projects")
                                .contentType("application/json")
                                .content(requestBody))
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409))
                                .andExpect(jsonPath("$.title").value("Data conflict"));

                verify(createProjectUseCase)
                                .execute("Proyecto de prueba", "Descripción del proyecto");
        }

        @Test
        void shouldReturn201WhenProjectIsCreatedSuccessfully() throws Exception {
                Project project = Project.create(
                                "Proyecto de prueba",
                                "Descripción del proyecto");

                when(createProjectUseCase.execute(
                                "Proyecto de prueba",
                                "Descripción del proyecto"))
                                .thenReturn(project);

                String requestBody = """
                                {
                                    "name": "Proyecto de prueba",
                                    "description": "Descripción del proyecto"
                                }
                                """;

                mockMvc.perform(post("/api/projects")
                                .contentType("application/json")
                                .content(requestBody))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(project.getId().toString()))
                                .andExpect(jsonPath("$.name").value("Proyecto de prueba"))
                                .andExpect(jsonPath("$.description")
                                                .value("Descripción del proyecto"))
                                .andExpect(jsonPath("$.createdAt").exists());

                verify(createProjectUseCase).execute(
                                "Proyecto de prueba",
                                "Descripción del proyecto");
        }

        @Test
        void shouldReturn200WhenProjectsAreRetrievedSuccessfully() throws Exception {
                Project project = Project.create(
                                "Proyecto de prueba",
                                "Descripción del proyecto");

                when(getAllProjectsUseCase.execute())
                                .thenReturn(java.util.List.of(project));

                mockMvc.perform(get("/api/projects"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].id")
                                                .value(project.getId().toString()))
                                .andExpect(jsonPath("$[0].name")
                                                .value("Proyecto de prueba"))
                                .andExpect(jsonPath("$[0].description")
                                                .value("Descripción del proyecto"));

                verify(getAllProjectsUseCase).execute();
        }

        @Test
        void shouldReturn200WhenProjectIsRetrievedSuccessfully() throws Exception {
                Project project = Project.create(
                                "Proyecto de prueba",
                                "Descripción del proyecto");

                when(getProjectByIdUseCase.execute(project.getId()))
                                .thenReturn(project);

                mockMvc.perform(get("/api/projects/{id}", project.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id")
                                                .value(project.getId().toString()))
                                .andExpect(jsonPath("$.name")
                                                .value("Proyecto de prueba"))
                                .andExpect(jsonPath("$.description")
                                                .value("Descripción del proyecto"))
                                .andExpect(jsonPath("$.createdAt").exists());

                verify(getProjectByIdUseCase).execute(project.getId());
        }

        @Test
        void shouldReturn200WhenProjectIsUpdatedSuccessfully() throws Exception {
                Project project = Project.create(
                                "Proyecto actualizado",
                                "Descripción actualizada");

                when(updateProjectUseCase.execute(
                                project.getId(),
                                "Proyecto actualizado",
                                "Descripción actualizada"))
                                .thenReturn(project);

                String requestBody = """
                                {
                                    "name": "Proyecto actualizado",
                                    "description": "Descripción actualizada"
                                }
                                """;

                mockMvc.perform(put("/api/projects/{id}", project.getId())
                                .contentType("application/json")
                                .content(requestBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id")
                                                .value(project.getId().toString()))
                                .andExpect(jsonPath("$.name")
                                                .value("Proyecto actualizado"))
                                .andExpect(jsonPath("$.description")
                                                .value("Descripción actualizada"))
                                .andExpect(jsonPath("$.createdAt").exists());

                verify(updateProjectUseCase).execute(
                                project.getId(),
                                "Proyecto actualizado",
                                "Descripción actualizada");
        }

        @Test
        void shouldReturn204WhenProjectIsDeletedSuccessfully() throws Exception {
                Project project = Project.create(
                                "Proyecto por eliminar",
                                "Descripción del proyecto");

                mockMvc.perform(delete("/api/projects/{id}", project.getId()))
                                .andExpect(status().isNoContent())
                                .andExpect(content().string(""));

                verify(deleteProjectUseCase).execute(project.getId());
        }
}