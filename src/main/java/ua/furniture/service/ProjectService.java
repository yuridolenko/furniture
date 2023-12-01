package ua.furniture.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.furniture.domain.Project;
import ua.furniture.exception.AccountNotFoundException;
import ua.furniture.repository.ProjectRepository;
import ua.furniture.web.dto.ProjectDTO;

import java.util.Optional;

@Service
public class ProjectService {

    private ProjectRepository projectRepository;

    private AccountService accountService;

    public Optional<Project> get(String id) {
        return projectRepository
                .findById(id);
    }

    public Project create(ProjectDTO projectDto) {
        var account = accountService
                .get(projectDto.getAccountId())
                .orElseThrow(AccountNotFoundException::new);
        var project = new Project(
                null,
                projectDto.getName(),
                account
        );
        return projectRepository.save(project);
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Optional<Project> getByAccountId(String accountId) {
        var account = accountService.get(accountId)
                .orElseThrow(AccountNotFoundException::new);
        return projectRepository
                .findByAccount(account);
    }
}
