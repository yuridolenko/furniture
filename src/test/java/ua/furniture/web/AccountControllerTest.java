package ua.furniture.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import ua.furniture.domain.Account;
import ua.furniture.service.AccountService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest extends BaseControllerTest {

    @MockBean
    private AccountService accountService;

    @Test
    void get_whenAccountExists_returns200WithBody() throws Exception {
        var account = new Account("1", "Johnson", "New York", "+12025550123");
        when(accountService.get("1")).thenReturn(Optional.of(account));

        mockMvc.perform(get("/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Johnson"));
    }

    @Test
    void get_whenAccountNotFound_returns404() throws Exception {
        when(accountService.get("99")).thenReturn(Optional.empty());

        mockMvc.perform(get("/account/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_returnsListWith200() throws Exception {
        var accounts = List.of(
                new Account("1", "Johnson", "New York", "+12025550123"),
                new Account("2", "Smith", "Los Angeles", "+13105550199")
        );
        when(accountService.findAll()).thenReturn(accounts);

        mockMvc.perform(get("/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void create_returnsCreatedAccountWith200() throws Exception {
        var input = new Account(null, "Johnson", "New York", "+12025550123");
        var saved = new Account("1", "Johnson", "New York", "+12025550123");
        when(accountService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Johnson"));
    }
}
