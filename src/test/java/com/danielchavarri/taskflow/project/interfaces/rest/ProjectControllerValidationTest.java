
package com.danielchavarri.taskflow.project.interfaces.rest;

import com.danielchavarri.taskflow.common.interfaces.rest.GlobalExceptionHandler;
import com.danielchavarri.taskflow.project.application.CreateProjectUseCase;
import com.danielchavarri.taskflow.project.application.DeleteProjectUseCase;
import com.danielchavarri.taskflow.project.application.GetAllProjectsUseCase;
import com.danielchavarri.taskflow.project.application.GetProjectByIdUseCase;
import com.danielchavarri.taskflow.project.application.UpdateProjectUseCase;
import com.danielchavarri.taskflow.project.domain.model.Project;
import com.danielchavarri.taskflow.shared.infrastructure.security.JwtService;
import com.danielchavarri.taskflow.shared.infrastructure.security.RestAuthenticationEntryPoint;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
                GlobalExceptionHandler.class,
                RestAuthenticationEntryPoint.class
})
class ProjectControllerValidationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserRepository userRepository;

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

        private final UUID ownerId = UUID.randomUUID();

        /**
         * Simula un usuario autenticado cuyo principal es un UUID.
         * Se establece directamente el SecurityContext porque los filtros
         * de seguridad están desactivados en esta prueba.
         */
        private RequestPostProcessor authenticated() {
                return request -> {
                        var authentication = new UsernamePasswordAuthenticationToken(
                                        ownerId,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_USER")));

                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);

                        return request;
                };
        }

        @Test
        void shouldReturn400WhenProjectNameIsBlank() throws Exception {

                String requestBody = """
                                {
                                    "name": "",
                                    "description": "Proyecto de prueba"
                                }
                                """;

                mockMvc.perform(post("/api/projects")
                                .with(authenticated())
                                .contentType(APPLICATION_JSON)
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
                                .with(authenticated())
                                .contentType(APPLICATION_JSON)
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
                                .with(authenticated())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.title").value("Validation failed"))
                                .andExpect(jsonPath("$.errors.description")
                                                .value("Máximo 500 caracteres"));

                verifyNoInteractions(createProjectUseCase);
        }

        @Test
        void shouldReturn400WhenRequestBodyContainsMalformedJson()
                        throws Exception {

                String malformedJson = """
                                {
                                    "name": "Proyecto de prueba",
                                    "description":
                                }
                                """;

                mockMvc.perform(post("/api/projects")
                                .with(authenticated())
                                .contentType(APPLICATION_JSON)
                                .content(malformedJson))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.title")
                                                .value("Malformed request"));

                verifyNoInteractions(createProjectUseCase);
        }

        @Test
        void shouldReturn201WhenProjectIsCreatedSuccessfully()
                        throws Exception {

                Project project = Project.create(
                                ownerId,
                                "Proyecto de prueba",
                                "Descripción del proyecto");

                when(createProjectUseCase.execute(
                                ownerId,
                                "Proyecto de prueba",
                                "Descripción del proyecto")).thenReturn(project);

                String requestBody = """
                                {
                                    "name": "Proyecto de prueba",
                                    "description": "Descripción del proyecto"
                                }
                                """;

                mockMvc.perform(post("/api/projects")
                                .with(authenticated())
                                .contentType(APPLICATION_JSON)
                                .content(requestBody))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id")
                                                .value(project.getId().toString()))
                                .andExpect(jsonPath("$.name")
                                                .value("Proyecto de prueba"))
                                .andExpect(jsonPath("$.description")
                                                .value("Descripción del proyecto"))
                                .andExpect(jsonPath("$.createdAt").exists());

                verify(createProjectUseCase).execute(
                                ownerId,
                                "Proyecto de prueba",
                                "Descripción del proyecto");
        }

        @Test
        void shouldReturn200WhenProjectsAreRetrievedSuccessfully()
                        throws Exception {

                Project project = Project.create(
                                ownerId,
                                "Proyecto de prueba",
                                "Descripción del proyecto");

                when(getAllProjectsUseCase.execute(ownerId))
                                .thenReturn(List.of(project));

                mockMvc.perform(get("/api/projects")
                                .with(authenticated()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].id")
                                                .value(project.getId().toString()))
                                .andExpect(jsonPath("$[0].name")
                                                .value("Proyecto de prueba"))
                                .andExpect(jsonPath("$[0].description")
                                                .value("Descripción del proyecto"));

                verify(getAllProjectsUseCase).execute(ownerId);
        }

        @Test
        void shouldReturn200WhenProjectIsRetrievedSuccessfully()
                        throws Exception {

                Project project = Project.create(
                                ownerId,
                                "Proyecto de prueba",
                                "Descripción del proyecto");

                when(getProjectByIdUseCase.execute(
                                project.getId(),
                                ownerId)).thenReturn(project);

                mockMvc.perform(get("/api/projects/{id}", project.getId())
                                .with(authenticated()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id")
                                                .value(project.getId().toString()))
                                .andExpect(jsonPath("$.name")
                                                .value("Proyecto de prueba"))
                                .andExpect(jsonPath("$.description")
                                                .value("Descripción del proyecto"))
                                .andExpect(jsonPath("$.createdAt").exists());

                verify(getProjectByIdUseCase).execute(
                                project.getId(),
                                ownerId);
        }

        @Test
        void shouldReturn200WhenProjectIsUpdatedSuccessfully()
                        throws Exception {

                Project project = Project.create(
                                ownerId,
                                "Proyecto actualizado",
                                "Descripción actualizada");

                when(updateProjectUseCase.execute(
                                project.getId(),
                                ownerId,
                                "Proyecto actualizado",
                                "Descripción actualizada")).thenReturn(project);

                String requestBody = """
                                {
                                    "name": "Proyecto actualizado",
                                    "description": "Descripción actualizada"
                                }
                                """;

                mockMvc.perform(put("/api/projects/{id}", project.getId())
                                .with(authenticated())
                                .contentType(APPLICATION_JSON)
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
                                ownerId,
                                "Proyecto actualizado",
                                "Descripción actualizada");
        }

        @Test
        void shouldReturn204WhenProjectIsDeletedSuccessfully()
                        throws Exception {

                Project project = Project.create(
                                ownerId,
                                "Proyecto por eliminar",
                                "Descripción del proyecto");

                mockMvc.perform(delete("/api/projects/{id}", project.getId())
                                .with(authenticated()))
                                .andExpect(status().isNoContent())
                                .andExpect(content().string(""));

                verify(deleteProjectUseCase).execute(
                                project.getId(),
                                ownerId);
        }
}