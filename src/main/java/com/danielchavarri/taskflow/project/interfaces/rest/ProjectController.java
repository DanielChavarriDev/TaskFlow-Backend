
package com.danielchavarri.taskflow.project.interfaces.rest;

import com.danielchavarri.taskflow.project.application.CreateProjectUseCase;
import com.danielchavarri.taskflow.project.application.DeleteProjectUseCase;
import com.danielchavarri.taskflow.project.application.GetAllProjectsUseCase;
import com.danielchavarri.taskflow.project.application.GetProjectByIdUseCase;
import com.danielchavarri.taskflow.project.application.UpdateProjectUseCase;
import com.danielchavarri.taskflow.project.domain.model.Project;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final CreateProjectUseCase createProjectUseCase;
    private final GetAllProjectsUseCase getAllProjectsUseCase;
    private final GetProjectByIdUseCase getProjectByIdUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final DeleteProjectUseCase deleteProjectUseCase;

    public ProjectController(
            CreateProjectUseCase createProjectUseCase,
            GetAllProjectsUseCase getAllProjectsUseCase,
            GetProjectByIdUseCase getProjectByIdUseCase,
            UpdateProjectUseCase updateProjectUseCase,
            DeleteProjectUseCase deleteProjectUseCase) {
        this.createProjectUseCase = createProjectUseCase;
        this.getAllProjectsUseCase = getAllProjectsUseCase;
        this.getProjectByIdUseCase = getProjectByIdUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.deleteProjectUseCase = deleteProjectUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(
            @Valid @RequestBody CreateProjectRequest request) {

        UUID ownerId = getAuthenticatedUserId();

        Project project = createProjectUseCase.execute(
                ownerId,
                request.name(),
                request.description());

        return ProjectResponse.from(project);
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        UUID ownerId = getAuthenticatedUserId();

        return getAllProjectsUseCase.execute(ownerId)
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable UUID id) {
        UUID ownerId = getAuthenticatedUserId();

        return ProjectResponse.from(
                getProjectByIdUseCase.execute(id, ownerId));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProjectRequest request) {

        UUID ownerId = getAuthenticatedUserId();

        Project updatedProject = updateProjectUseCase.execute(
                id,
                ownerId,
                request.name(),
                request.description());

        return ProjectResponse.from(updatedProject);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        UUID ownerId = getAuthenticatedUserId();
        deleteProjectUseCase.execute(id, ownerId);
    }

    private UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UUID userId)
                || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "No se pudo obtener el usuario autenticado.");
        }

        return userId;
    }
}