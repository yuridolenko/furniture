package ua.furniture.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.furniture.domain.Account;
import ua.furniture.domain.Project;
import ua.furniture.repository.AccountRepository;
import ua.furniture.repository.ProjectRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {
    private AccountRepository accountRepository;

    public Optional<Account> get(String id) {
        return accountRepository
                .findById(id);
    }

    public Account create(Account account) {
        return accountRepository.save(account);
    }

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public List<Account> findByName(String name) {
        return accountRepository.findByName(name);
    }

    @Autowired
    public void setAccountRepository(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
}
