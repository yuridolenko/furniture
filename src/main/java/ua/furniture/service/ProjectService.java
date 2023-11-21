package ua.furniture.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.furniture.domain.Project;
import ua.furniture.repository.ProjectRepository;

import java.util.Optional;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    public Optional<Project> get(String id) {
        return projectRepository
                .findById(id);
    }

    public Project create(Project project) {
        return projectRepository.save(project);
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
}
