package com.hivetech.kanban.controller;

import com.hivetech.kanban.dto.AuthResponse;
import com.hivetech.kanban.dto.LoginRequest;
import com.hivetech.kanban.dto.RegisterRequest;
import com.hivetech.kanban.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public Map<String, AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthResponse token = authService.register(request);
        return Map.of("token", token);
    }

    @PostMapping("/login")
    public Map<String, AuthResponse> login(
            @RequestBody LoginRequest request
    ) {
        AuthResponse token = authService.login(request, authenticationManager);
        return Map.of("token", token);
    }
}