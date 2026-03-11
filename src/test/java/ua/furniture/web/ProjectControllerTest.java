package ua.furniture.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;
import ua.furniture.exception.AccountNotFoundException;
import ua.furniture.service.ProjectService;
import ua.furniture.web.dto.ProjectResponse;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest extends BaseControllerTest {

    @MockBean
    private ProjectService projectService;

    private Project project;

    @BeforeEach
    void setUp() {
        var account = new Account("a1", "Johnson", "New York", "+12025550123");
        project = new Project("p1", "Kitchen", account);
    }

    @Test
    void get_whenProjectExists_returns200WithBody() throws Exception {
        when(projectService.get("p1")).thenReturn(Optional.of(project));

        mockMvc.perform(get("/project/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Kitchen"));
    }

    @Test
    void get_whenProjectNotFound_returns404() throws Exception {
        when(projectService.get("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/project/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByAccount_whenProjectsExist_returns200WithList() throws Exception {
        var project2 = new Project("p2", "Living Room", new Account("a1", "Johnson", "New York", "+12025550123"));
        when(projectService.getByAccountId("a1")).thenReturn(List.of(project, project2));

        mockMvc.perform(get("/project/account/a1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p1"))
                .andExpect(jsonPath("$[1].id").value("p2"));
    }

    @Test
    void getByAccount_whenNoProjects_returns200WithEmptyList() throws Exception {
        when(projectService.getByAccountId("a1")).thenReturn(List.of());

        mockMvc.perform(get("/project/account/a1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getByAccount_whenAccountNotFound_returns404() throws Exception {
        when(projectService.getByAccountId("bad")).thenThrow(AccountNotFoundException.class);

        mockMvc.perform(get("/project/account/bad"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returnsCreatedProjectWith200() throws Exception {
        var dto = new ProjectResponse("Kitchen", "a1");
        when(projectService.create(any())).thenReturn(project);

        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.name").value("Kitchen"));
    }

    @Test
    void create_whenAccountNotFound_returns404() throws Exception {
        var dto = new ProjectResponse("Kitchen", "bad-id");
        when(projectService.create(any())).thenThrow(AccountNotFoundException.class);

        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // --- Security: unauthenticated access ---

    @Test
    @WithAnonymousUser
    void get_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/project/p1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void getByAccount_withoutAuth_returns401() throws Exception {
        mockMvc.perform(get("/project/account/a1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithAnonymousUser
    void create_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}
