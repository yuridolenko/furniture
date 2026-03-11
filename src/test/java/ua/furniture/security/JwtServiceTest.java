package ua.furniture.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET =
            "dGhpcyBpcyBhIHZlcnkgbG9uZyBhbmQgc2VjdXJlIHNlY3JldCBrZXkgZm9yIEpXVCB0b2tlbnM=";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expirationMs", 86400000L);
        userDetails = User.withUsername("john").password("hash").roles("USER").build();
    }

    @Test
    void generateToken_embeds_correctUsername() {
        // Act
        var token = jwtService.generateToken(userDetails);

        // Assert
        assertThat(jwtService.extractUsername(token)).isEqualTo("john");
    }

    @Test
    void isTokenValid_withValidToken_returnsTrue() {
        // Arrange
        var token = jwtService.generateToken(userDetails);

        // Act & Assert
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void isTokenValid_withDifferentUser_returnsFalse() {
        // Arrange
        var token = jwtService.generateToken(userDetails);
        var otherUser = User.withUsername("other").password("hash").roles("USER").build();

        // Act & Assert
        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    void isTokenValid_withExpiredToken_returnsFalse() {
        // Arrange — generate token that is already expired
        var expiredService = new JwtService();
        ReflectionTestUtils.setField(expiredService, "secret", SECRET);
        ReflectionTestUtils.setField(expiredService, "expirationMs", -1000L);
        var token = expiredService.generateToken(userDetails);

        // Act & Assert
        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    void isTokenValid_withTamperedToken_returnsFalse() {
        // Arrange
        var token = jwtService.generateToken(userDetails);
        var tampered = token.substring(0, token.length() - 8) + "TAMPERED";

        // Act & Assert
        assertThat(jwtService.isTokenValid(tampered, userDetails)).isFalse();
    }

    @Test
    void extractUsername_withExpiredToken_throwsJwtException() {
        // Arrange
        var expiredService = new JwtService();
        ReflectionTestUtils.setField(expiredService, "secret", SECRET);
        ReflectionTestUtils.setField(expiredService, "expirationMs", -1000L);
        var token = expiredService.generateToken(userDetails);

        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void extractUsername_withTamperedToken_throwsJwtException() {
        // Arrange
        var token = jwtService.generateToken(userDetails);
        var tampered = token.substring(0, token.length() - 8) + "TAMPERED";

        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername(tampered))
                .isInstanceOf(JwtException.class);
    }

    @Test
    void extractUsername_withCompletelyRandomString_throwsJwtException() {
        // Act & Assert
        assertThatThrownBy(() -> jwtService.extractUsername("not.a.jwt"))
                .isInstanceOf(JwtException.class);
    }
}
