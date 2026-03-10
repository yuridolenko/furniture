package ua.furniture.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;
import ua.furniture.exception.AccountNotFoundException;
import ua.furniture.service.ProjectService;
import ua.furniture.web.dto.ProjectDTO;

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
    void getByAccount_whenProjectExists_returns200() throws Exception {
        when(projectService.getByAccountId("a1")).thenReturn(Optional.of(project));

        mockMvc.perform(get("/project/account/a1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p1"));
    }

    @Test
    void getByAccount_whenAccountNotFound_returns404() throws Exception {
        when(projectService.getByAccountId("bad")).thenThrow(AccountNotFoundException.class);

        mockMvc.perform(get("/project/account/bad"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByAccount_whenProjectNotFound_returns404() throws Exception {
        when(projectService.getByAccountId("a1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/project/account/a1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returnsCreatedProjectWith200() throws Exception {
        var dto = new ProjectDTO("Kitchen", "a1");
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
        var dto = new ProjectDTO("Kitchen", "bad-id");
        when(projectService.create(any())).thenThrow(AccountNotFoundException.class);

        mockMvc.perform(post("/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }
}
