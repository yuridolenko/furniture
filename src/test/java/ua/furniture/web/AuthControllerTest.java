package ua.furniture.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithAnonymousUser;
import ua.furniture.domain.User;
import ua.furniture.exception.UserAlreadyExistsException;
import ua.furniture.web.dto.LoginRequest;
import ua.furniture.web.dto.RegisterRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends BaseControllerTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Test
    @WithAnonymousUser
    void register_withNewUser_returns200WithToken() throws Exception {
        // Arrange
        var user = new User("1", "john", "hashed");
        when(userService.register("john", "pass123")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("john", "pass123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    @WithAnonymousUser
    void register_withExistingUser_returns409() throws Exception {
        // Arrange
        when(userService.register("john", "pass123")).thenThrow(new UserAlreadyExistsException("john"));

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegisterRequest("john", "pass123"))))
                .andExpect(status().isConflict());
    }

    @Test
    @WithAnonymousUser
    void login_withValidCredentials_returns200WithToken() throws Exception {
        // Arrange
        var user = new User("1", "john", "hashed");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.loadUserByUsername("john")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("john", "pass123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    @WithAnonymousUser
    void login_withWrongPassword_returns401() throws Exception {
        // Arrange
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("john", "wrong"))))
                .andExpect(status().isUnauthorized());
    }
}
