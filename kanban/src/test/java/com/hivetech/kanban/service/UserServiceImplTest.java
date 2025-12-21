package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.UpdateUserRequest;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl service;

    @Test
    void shouldUpdateUser() {
        User user = new User();
        user.setPassword("encoded");
        user.setEmail("old@test.com");

        UpdateUserRequest req = new UpdateUserRequest();
        req.setCurrentPassword("old");
        req.setEmail("new@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old", "encoded")).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);

        User result = service.updateUser(1L, req);

        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    void shouldThrowWhenCurrentPasswordInvalid() {
        User user = new User();
        user.setPassword("encoded");

        UpdateUserRequest req = new UpdateUserRequest();
        req.setCurrentPassword("wrong");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThatThrownBy(() -> service.updateUser(1L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid current password");
    }

    @Test
    void shouldThrowWhenEmailAlreadyUsed() {
        User user = new User();
        user.setPassword("encoded");
        user.setEmail("old@test.com");

        UpdateUserRequest req = new UpdateUserRequest();
        req.setCurrentPassword("pass");
        req.setEmail("new@test.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(true);

        assertThatThrownBy(() -> service.updateUser(1L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already in use");
    }

}
