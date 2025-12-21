package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.AuthResponse;
import com.hivetech.kanban.dto.LoginRequest;
import com.hivetech.kanban.dto.RegisterRequest;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtUtil jwtUtil;

    @Mock
    AuthenticationManager authenticationManager;

    @InjectMocks
    AuthService authService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");
        request.setName("Test");
        request.setPassword("password");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded");
        when(jwtUtil.generateToken("test@test.com")).thenReturn("jwt");

        AuthResponse response = authService.register(request);

        assertThat(response.email()).isEqualTo("test@test.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@test.com");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldLoginUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("pass");

        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test@test.com"))
                .thenReturn("jwt");

        AuthResponse response =
                authService.login(request, authenticationManager);

        assertThat(response.token()).isEqualTo("jwt");
    }

    @Test
    void shouldThrowWhenUserNotFoundOnLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@test.com");
        request.setPassword("pass");

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.login(request, authenticationManager)
        ).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldEncodePasswordOnRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("x@test.com");
        request.setName("X");
        request.setPassword("plain");

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(jwtUtil.generateToken(any())).thenReturn("jwt");

        authService.register(request);

        verify(passwordEncoder).encode("plain");
    }
}
