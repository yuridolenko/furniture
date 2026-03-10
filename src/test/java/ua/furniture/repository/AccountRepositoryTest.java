package ua.furniture.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import ua.furniture.domain.Account;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class AccountRepositoryTest extends AbstractRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void save_persistsAccount() {
        var account = new Account(null, "Johnson", "New York", "+12025550123");

        var saved = accountRepository.save(account);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Johnson");
        assertThat(saved.getAddress()).isEqualTo("New York");
        assertThat(saved.getPhone()).isEqualTo("+12025550123");
    }

    @Test
    void findById_whenExists_returnsAccount() {
        var saved = accountRepository.save(new Account(null, "Johnson", "New York", "+12025550123"));

        var result = accountRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Johnson");
    }

    @Test
    void findById_whenNotExists_returnsEmpty() {
        var result = accountRepository.findById("non-existent-id");

        assertThat(result).isEmpty();
    }

    @Test
    void findAll_returnsAllAccounts() {
        accountRepository.saveAll(List.of(
                new Account(null, "Johnson", "New York", "+12025550123"),
                new Account(null, "Smith", "Los Angeles", "+13105550199")
        ));

        var result = accountRepository.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findByName_whenMatch_returnsAccounts() {
        accountRepository.saveAll(List.of(
                new Account(null, "Johnson", "New York", "+12025550123"),
                new Account(null, "Johnson", "Chicago", "+13125550100"),
                new Account(null, "Smith", "Los Angeles", "+13105550199")
        ));

        var result = accountRepository.findByName("Johnson");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(a -> a.getName().equals("Johnson"));
    }

    @Test
    void findByName_whenNoMatch_returnsEmpty() {
        accountRepository.save(new Account(null, "Johnson", "New York", "+12025550123"));

        var result = accountRepository.findByName("Unknown");

        assertThat(result).isEmpty();
    }

    @Test
    void delete_removesAccount() {
        var saved = accountRepository.save(new Account(null, "Johnson", "New York", "+12025550123"));

        accountRepository.deleteById(saved.getId());

        assertThat(accountRepository.findById(saved.getId())).isEmpty();
    }
}
