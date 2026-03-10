package ua.furniture.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.furniture.domain.Account;
import ua.furniture.repository.AccountRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void get_whenAccountExists_returnsAccount() {
        var account = new Account("1", "Johnson", "New York", "+12025550123");
        when(accountRepository.findById("1")).thenReturn(Optional.of(account));

        var result = accountService.get("1");

        assertTrue(result.isPresent());
        assertEquals("Johnson", result.get().getName());
    }

    @Test
    void get_whenAccountNotExists_returnsEmpty() {
        when(accountRepository.findById("99")).thenReturn(Optional.empty());

        var result = accountService.get("99");

        assertTrue(result.isEmpty());
    }

    @Test
    void create_savesAndReturnsAccount() {
        var input = new Account(null, "Johnson", "New York", "+12025550123");
        var saved = new Account("1", "Johnson", "New York", "+12025550123");
        when(accountRepository.save(input)).thenReturn(saved);

        var result = accountService.create(input);

        assertEquals("1", result.getId());
        verify(accountRepository).save(input);
    }

    @Test
    void findAll_returnsAllAccounts() {
        var accounts = List.of(
                new Account("1", "Johnson", "New York", "+12025550123"),
                new Account("2", "Smith", "Los Angeles", "+13105550199")
        );
        when(accountRepository.findAll()).thenReturn(accounts);

        var result = accountService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void findAll_whenEmpty_returnsEmptyList() {
        when(accountRepository.findAll()).thenReturn(List.of());

        var result = accountService.findAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void findByName_returnsMatchingAccounts() {
        var accounts = List.of(new Account("1", "Johnson", "New York", "+12025550123"));
        when(accountRepository.findByName("Johnson")).thenReturn(accounts);

        var result = accountService.findByName("Johnson");

        assertEquals(1, result.size());
        assertEquals("Johnson", result.get(0).getName());
    }
}
