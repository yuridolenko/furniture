package ua.furniture.web;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.furniture.repository.AccountRepository;
import ua.furniture.repository.ProjectRepository;

@TestConfiguration
class WebTestConfiguration {

    @MockBean
    AccountRepository accountRepository;

    @MockBean
    ProjectRepository projectRepository;
}
