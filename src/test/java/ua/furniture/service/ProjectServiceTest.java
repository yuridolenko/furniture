package ua.furniture.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;
import ua.furniture.exception.AccountNotFoundException;
import ua.furniture.repository.ProjectRepository;
import ua.furniture.web.dto.ProjectDTO;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void get_whenProjectExists_returnsProject() {
        var account = new Account("a1", "Johnson", "New York", "+12025550123");
        var project = new Project("p1", "Kitchen", account);
        when(projectRepository.findById("p1")).thenReturn(Optional.of(project));

        var result = projectService.get("p1");

        assertTrue(result.isPresent());
        assertEquals("Kitchen", result.get().getName());
    }

    @Test
    void get_whenProjectNotExists_returnsEmpty() {
        when(projectRepository.findById("100")).thenReturn(Optional.empty());

        var result = projectService.get("100");

        assertTrue(result.isEmpty());
    }

    @Test
    void create_whenAccountExists_savesProject() {
        var account = new Account("a1", "Johnson", "New York", "+12025550123");
        var dto = new ProjectDTO("Kitchen", "a1");
        var saved = new Project("p1", "Kitchen", account);
        when(accountService.get("a1")).thenReturn(Optional.of(account));
        when(projectRepository.save(any())).thenReturn(saved);

        var result = projectService.create(dto);

        assertEquals("p1", result.getId());
        assertEquals("Kitchen", result.getName());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void create_whenAccountNotFound_throwsAccountNotFoundException() {
        var dto = new ProjectDTO("Bedroom", "bad-id");
        when(accountService.get("bad-id")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> projectService.create(dto));
        verify(projectRepository, never()).save(any());
    }

    @Test
    void getByAccountId_whenAccountAndProjectsExist_returnsProjects() {
        var account = new Account("a1", "Johnson", "New York", "+12025550123");
        var project1 = new Project("p1", "Kitchen", account);
        var project2 = new Project("p2", "Living Room", account);
        when(accountService.get("a1")).thenReturn(Optional.of(account));
        when(projectRepository.findByAccount(account)).thenReturn(List.of(project1, project2));

        var result = projectService.getByAccountId("a1");

        assertEquals(2, result.size());
        assertEquals("p1", result.get(0).getId());
        assertEquals("p2", result.get(1).getId());
    }

    @Test
    void getByAccountId_whenAccountNotFound_throwsAccountNotFoundException() {
        when(accountService.get("bad-id")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> projectService.getByAccountId("bad-id"));
    }
}
