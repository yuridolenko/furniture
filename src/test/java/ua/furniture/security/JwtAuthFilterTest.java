package ua.furniture.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import ua.furniture.domain.User;
import ua.furniture.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UserService userService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain chain;

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_withNoAuthHeader_passesThrough() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withNonBearerHeader_passesThrough() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        verifyNoInteractions(jwtService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withValidToken_setsAuthenticationAndPassesThrough() throws Exception {
        // Arrange
        var user = new User("1", "john", "hash");
        when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("john");
        when(userService.loadUserByUsername("john")).thenReturn(user);
        when(jwtService.isTokenValid("valid.jwt.token", user)).thenReturn(true);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getName()).isEqualTo("john");
    }

    @Test
    void doFilterInternal_withInvalidToken_passesThrough_withoutAuth() throws Exception {
        // Arrange
        var user = new User("1", "john", "hash");
        when(request.getHeader("Authorization")).thenReturn("Bearer bad.token");
        when(jwtService.extractUsername("bad.token")).thenReturn("john");
        when(userService.loadUserByUsername("john")).thenReturn(user);
        when(jwtService.isTokenValid("bad.token", user)).thenReturn(false);

        // Act
        jwtAuthFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withExpiredToken_passesThrough_withoutAuth() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer expired.token");
        when(jwtService.extractUsername("expired.token")).thenThrow(new JwtException("Token expired"));

        // Act
        jwtAuthFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        verifyNoInteractions(userService);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void doFilterInternal_withExistingAuthentication_skipsTokenValidation() throws Exception {
        // Arrange
        var existing = new UsernamePasswordAuthenticationToken("existing", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(existing);
        when(request.getHeader("Authorization")).thenReturn("Bearer some.token");
        when(jwtService.extractUsername("some.token")).thenReturn("john");

        // Act
        jwtAuthFilter.doFilterInternal(request, response, chain);

        // Assert
        verify(chain).doFilter(request, response);
        verify(userService, never()).loadUserByUsername(any());
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("existing");
    }
}
