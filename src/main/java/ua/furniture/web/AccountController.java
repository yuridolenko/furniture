package ua.furniture.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ua.furniture.domain.Account;
import ua.furniture.exception.AccountNotFoundException;
import ua.furniture.service.AccountService;

import java.util.List;

@RestController
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("account/{id}")
    public Account get(@PathVariable("id") String id) {
        return accountService
                .get(id)
                .orElseThrow(AccountNotFoundException::new);
    }

    @GetMapping("account")
    public List<Account> getAll() {
        return accountService
                .findAll();
    }

    @PostMapping("account")
    public Account create(@RequestBody Account account) {
        return accountService.create(account);
    }

}
