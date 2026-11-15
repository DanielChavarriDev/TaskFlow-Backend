
package com.danielchavarri.taskflow.shared.infrastructure.security;

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
import com.danielchavarri.taskflow.task.application.UpdateTaskService;
import com.danielchavarri.taskflow.task.interfaces.rest.TaskController;
import com.danielchavarri.taskflow.user.domain.model.User;
import com.danielchavarri.taskflow.user.domain.model.UserRole;
import com.danielchavarri.taskflow.user.domain.port.UserRepository;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import io.jsonwebtoken.Claims;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import com.danielchavarri.taskflow.task.domain.model.Task;
import com.danielchavarri.taskflow.task.domain.model.TaskPriority;
import com.danielchavarri.taskflow.task.domain.model.TaskStatus;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
@EnableWebSecurity
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        RestAuthenticationEntryPoint.class
})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

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

    private User user;
    private User admin;

    @BeforeEach
    void setUp() {
        user = createUser(UserRole.USER);
        admin = createUser(UserRole.ADMIN);
    }

    private User createUser(UserRole role) {
        return User.reconstitute(
                UUID.randomUUID(),
                role == UserRole.ADMIN ? "Administrator" : "Daniel",
                role == UserRole.ADMIN
                        ? "admin@example.com"
                        : "daniel@example.com",
                "$2a$12$encodedPasswordHash",
                role,
                Instant.now());
    }

    private void mockValidToken(String token, User account) {
        Claims claims = mock(Claims.class);

        when(jwtService.validateToken(token))
                .thenReturn(claims);

        when(claims.getSubject())
                .thenReturn(account.getId().toString());

        when(userRepository.findById(account.getId()))
                .thenReturn(Optional.of(account));
    }

    private Task createTask(TaskStatus status) {
        return Task.reconstitute(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Implementar funcionalidad",
                "Tarea de prueba para operaciones administrativas",
                status,
                TaskPriority.MEDIUM,
                LocalDate.now().plusDays(7),
                Instant.now());
    }

    @Test
    void shouldReturn401WhenTokenIsMissing() throws Exception {
        mockMvc.perform(get("/api/tasks/admin/pending-review"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"));

        verifyNoInteractions(getTasksPendingReviewService);
        verifyNoInteractions(jwtService, userRepository);
    }

    @Test
    void shouldReturn403WhenUserAccessesAdminEndpoint() throws Exception {
        mockValidToken("user-token", user);

        mockMvc.perform(get("/api/tasks/admin/pending-review")
                .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(
                        "application/problem+json"))
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.title").value("Access denied"));

        verifyNoInteractions(getTasksPendingReviewService);
    }

    @Test
    void shouldAllowAdminToAccessAdminEndpoint() throws Exception {
        mockValidToken("admin-token", admin);

        when(getTasksPendingReviewService.execute())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/tasks/admin/pending-review")
                .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(getTasksPendingReviewService).execute();
    }

    @Test
    void shouldAllowAuthenticatedUserToAccessRegularEndpoint()
            throws Exception {

        mockValidToken("user-token", user);

        when(getFilteredTasksService.execute(
                user.getId(), null, null))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/tasks")
                .header("Authorization", "Bearer user-token"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(getFilteredTasksService).execute(
                user.getId(), null, null);
    }

    @Test
    void shouldReturn403WhenUserIntervenesTask() throws Exception {
        mockValidToken("user-token", user);

        UUID taskId = UUID.randomUUID();

        mockMvc.perform(patch("/api/tasks/{id}/admin-intervention", taskId)
                .header("Authorization", "Bearer user-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(adminInterveneTaskService);
    }

    @Test
    void shouldReturn403WhenUserApprovesTaskCompletion() throws Exception {
        mockValidToken("user-token", user);

        UUID taskId = UUID.randomUUID();

        mockMvc.perform(patch("/api/tasks/{id}/approve-completion", taskId)
                .header("Authorization", "Bearer user-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(approveTaskCompletionService);
    }

    @Test
    void shouldReturn403WhenUserRejectsTaskCompletion() throws Exception {
        mockValidToken("user-token", user);

        UUID taskId = UUID.randomUUID();

        mockMvc.perform(patch("/api/tasks/{id}/reject-completion", taskId)
                .header("Authorization", "Bearer user-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(rejectTaskCompletionService);
    }

    @Test
    void shouldReturn403WhenUserAccessesAdminInterventionHistory()
            throws Exception {

        mockValidToken("user-token", user);

        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/api/tasks/{id}/admin-interventions", taskId)
                .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));

        verifyNoInteractions(getTaskStatusAuditService);
    }

    @Test
    void shouldAllowAdminToAccessAdminInterventionHistory()
            throws Exception {

        mockValidToken("admin-token", admin);

        UUID taskId = UUID.randomUUID();

        when(getTaskStatusAuditService.execute(taskId))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/tasks/{id}/admin-interventions", taskId)
                .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(getTaskStatusAuditService).execute(taskId);
    }

    @Test
    void shouldAllowAdminToInterveneTask() throws Exception {
        mockValidToken("admin-token", admin);

        UUID taskId = UUID.randomUUID();
        String reason = "Intervención administrativa justificada.";

        Task task = createTask(TaskStatus.COMPLETED);

        when(adminInterveneTaskService.execute(
                taskId,
                admin.getId(),
                TaskStatus.COMPLETED,
                reason))
                .thenReturn(task);

        mockMvc.perform(patch("/api/tasks/{id}/admin-intervention", taskId)
                .header("Authorization", "Bearer admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "newStatus": "COMPLETED",
                            "reason": "Intervención administrativa justificada."
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(adminInterveneTaskService).execute(
                taskId,
                admin.getId(),
                TaskStatus.COMPLETED,
                reason);
    }

    @Test
    void shouldAllowAdminToApproveTaskCompletion() throws Exception {
        mockValidToken("admin-token", admin);

        UUID taskId = UUID.randomUUID();
        String reason = "La tarea cumple con los criterios establecidos.";

        Task task = createTask(TaskStatus.COMPLETED);

        when(approveTaskCompletionService.execute(
                taskId,
                admin.getId(),
                reason))
                .thenReturn(task);

        mockMvc.perform(patch("/api/tasks/{id}/approve-completion", taskId)
                .header("Authorization", "Bearer admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reason": "La tarea cumple con los criterios establecidos."
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(approveTaskCompletionService).execute(
                taskId,
                admin.getId(),
                reason);
    }

    @Test
    void shouldAllowAdminToRejectTaskCompletion() throws Exception {
        mockValidToken("admin-token", admin);

        UUID taskId = UUID.randomUUID();
        String reason = "La tarea requiere correcciones adicionales.";

        Task task = createTask(TaskStatus.IN_PROGRESS);

        when(rejectTaskCompletionService.execute(
                taskId,
                admin.getId(),
                reason))
                .thenReturn(task);

        mockMvc.perform(patch("/api/tasks/{id}/reject-completion", taskId)
                .header("Authorization", "Bearer admin-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "reason": "La tarea requiere correcciones adicionales."
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(rejectTaskCompletionService).execute(
                taskId,
                admin.getId(),
                reason);
    }
}