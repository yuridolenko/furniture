package ua.furniture.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.furniture.domain.User;
import ua.furniture.exception.UserAlreadyExistsException;
import ua.furniture.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void register_withNewUsername_savesUserWithHashedPassword() {
        // Arrange
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawPass")).thenReturn("hashedPass");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = userService.register("john", "rawPass");

        // Assert
        assertThat(result.getUsername()).isEqualTo("john");
        assertThat(result.getPassword()).isEqualTo("hashedPass");
    }

    @Test
    void register_withExistingUsername_throwsUserAlreadyExistsException() {
        // Arrange
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User("1", "john", "hash")));

        // Act & Assert
        assertThatThrownBy(() -> userService.register("john", "pass"))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        // Arrange
        var user = new User("1", "john", "hash");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        // Act
        var result = userService.loadUserByUsername("john");

        // Assert
        assertThat(result.getUsername()).isEqualTo("john");
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
