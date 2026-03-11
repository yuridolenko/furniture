package ua.furniture.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.furniture.domain.Project;
import ua.furniture.exception.ProjectNotFoundException;
import ua.furniture.service.ProjectService;
import ua.furniture.web.dto.ProjectResponse;

import java.util.List;

@RestController
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("project/{id}")
    public Project get(@PathVariable("id") String id) {
        return projectService
                .get(id)
                .orElseThrow(ProjectNotFoundException::new);
    }

    @GetMapping("project/account/{id}")
    public List<Project> getByAccount(@PathVariable("id") String accountId) {
        return projectService.getByAccountId(accountId);
    }

    @PostMapping("project")
    public Project create(@RequestBody ProjectResponse project) {
        return projectService.create(project);
    }
}
