package ua.furniture.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.furniture.domain.User;
import ua.furniture.exception.UserAlreadyExistsException;
import ua.furniture.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public User register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException(username);
        }
        var user = new User(null, username, passwordEncoder.encode(rawPassword));
        return userRepository.save(user);
    }
}
