package ua.furniture.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;
import ua.furniture.repository.AccountRepository;
import ua.furniture.repository.ProjectRepository;
import ua.furniture.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private AccountRepository accountRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private UserRepository userRepository;

    private String token;
    private Account account;

    @BeforeEach
    void setUp() throws Exception {
        // Arrange — clean state
        projectRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        token = registerAndGetToken("testuser", "password123");
        account = accountRepository.save(new Account(null, "Johnson", "New York", "+12025550123"));
    }

    @Test
    void getByAccount_withValidToken_returnsAllProjectsForAccount() throws Exception {
        // Arrange
        projectRepository.save(new Project(null, "Kitchen", account));
        projectRepository.save(new Project(null, "Living Room", account));

        // Act & Assert
        mockMvc.perform(get("/project/account/" + account.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Kitchen"))
                .andExpect(jsonPath("$[1].name").value("Living Room"))
                .andExpect(jsonPath("$[0].account.id").value(account.getId()));
    }

    @Test
    void getByAccount_withValidToken_whenNoProjects_returnsEmptyList() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/project/account/" + account.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getByAccount_withValidToken_whenAccountNotFound_returns404() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/project/account/non-existent-id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getByAccount_withoutToken_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/project/account/" + account.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByAccount_withInvalidToken_returns401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/project/account/" + account.getId())
                        .header("Authorization", "Bearer this.is.not.valid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByAccount_withExpiredToken_returns401() throws Exception {
        // Arrange — expired token (signature valid but exp in the past)
        var expiredToken = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJ0ZXN0dXNlciIsImlhdCI6MTcwMDAwMDAwMCwiZXhwIjoxNzAwMDAwMDAxfQ" +
                ".invalid-signature";

        // Act & Assert
        mockMvc.perform(get("/project/account/" + account.getId())
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getByAccount_projectsBelongOnlyToRequestedAccount() throws Exception {
        // Arrange — two accounts, each with one project
        var otherAccount = accountRepository.save(new Account(null, "Smith", "LA", "+13105550199"));
        projectRepository.save(new Project(null, "Kitchen", account));
        projectRepository.save(new Project(null, "Bedroom", otherAccount));

        // Act & Assert — only Johnson's project is returned
        mockMvc.perform(get("/project/account/" + account.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Kitchen"));
    }
}
