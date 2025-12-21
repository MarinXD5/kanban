package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.AuthResponse;
import com.hivetech.kanban.dto.LoginRequest;
import com.hivetech.kanban.dto.RegisterRequest;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setOrderIndex(0);

        userRepository.save(user);

        return new AuthResponse(
                jwtUtil.generateToken(user.getEmail()),
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAvatarColor()
        );
    }

    public AuthResponse login(LoginRequest request, AuthenticationManager authManager) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        return new AuthResponse(
                jwtUtil.generateToken(user.getEmail()),
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getAvatarColor()
        );
    }
}
