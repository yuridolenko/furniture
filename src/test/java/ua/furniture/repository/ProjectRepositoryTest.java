package ua.furniture.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class ProjectRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        accountRepository.deleteAll();
        account = accountRepository.save(new Account(null, "Johnson", "New York", "+12025550123"));
    }

    @Test
    void save_persistsProject() {
        var project = new Project(null, "Kitchen", account);

        var saved = projectRepository.save(project);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Kitchen");
    }

    @Test
    void findById_whenExists_returnsProject() {
        var saved = projectRepository.save(new Project(null, "Kitchen", account));

        var result = projectRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Kitchen");
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        var result = projectRepository.findById("non-existent-id");

        assertThat(result).isEmpty();
    }

    @Test
    void findByAccount_whenProjectsExist_returnsAllProjects() {
        projectRepository.save(new Project(null, "Kitchen", account));
        projectRepository.save(new Project(null, "Living Room", account));

        var result = projectRepository.findByAccount(account);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAccount().getId()).isEqualTo(account.getId());
    }

    @Test
    void findByAccount_whenNoProjects_returnsEmptyList() {
        var otherAccount = accountRepository.save(new Account(null, "Smith", "LA", "+13105550199"));

        var result = projectRepository.findByAccount(otherAccount);

        assertThat(result).isEmpty();
    }

    @Test
    void delete_removesProject() {
        var saved = projectRepository.save(new Project(null, "Kitchen", account));

        projectRepository.deleteById(saved.getId());

        assertThat(projectRepository.findById(saved.getId())).isEmpty();
    }
}
