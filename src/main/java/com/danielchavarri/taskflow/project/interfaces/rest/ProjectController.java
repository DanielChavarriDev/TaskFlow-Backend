package com.danielchavarri.taskflow.project.interfaces.rest;

import com.danielchavarri.taskflow.project.application.CreateProjectUseCase;
import com.danielchavarri.taskflow.project.application.DeleteProjectUseCase;
import com.danielchavarri.taskflow.project.application.GetAllProjectsUseCase;
import com.danielchavarri.taskflow.project.application.GetProjectByIdUseCase;
import com.danielchavarri.taskflow.project.application.UpdateProjectUseCase;
import com.danielchavarri.taskflow.project.domain.model.Project;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.List;

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
        Project project = createProjectUseCase.execute(
                request.name(),
                request.description());

        return ProjectResponse.from(project);
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        return getAllProjectsUseCase.execute()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse findById(@PathVariable UUID id) {
        return ProjectResponse.from(
                getProjectByIdUseCase.execute(id));
    }

    @PutMapping("/{id}")
    public ProjectResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProjectRequest request) {

        Project updatedProject = updateProjectUseCase.execute(
                id,
                request.name(),
                request.description());

        return ProjectResponse.from(updatedProject);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteProjectUseCase.execute(id);
    }
}