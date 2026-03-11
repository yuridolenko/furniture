package ua.furniture.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.furniture.security.JwtService;
import ua.furniture.service.UserService;
import ua.furniture.web.dto.AuthResponse;
import ua.furniture.web.dto.LoginRequest;
import ua.furniture.web.dto.RegisterRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        var user = userService.register(request.username(), request.password());
        return new AuthResponse(jwtService.generateToken(user));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        var userDetails = userService.loadUserByUsername(request.username());
        return new AuthResponse(jwtService.generateToken(userDetails));
    }
}
