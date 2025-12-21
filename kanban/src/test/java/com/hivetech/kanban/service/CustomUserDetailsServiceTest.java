package com.hivetech.kanban.service;

import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CustomUserDetailsServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    CustomUserDetailsService service;

    @Test
    void shouldLoadUser() {
        User user = new User();
        user.setEmail("a@a.com");
        user.setPassword("pass");

        when(userRepository.findByEmail("a@a.com"))
                .thenReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("a@a.com");

        assertThat(details.getUsername()).isEqualTo("a@a.com");
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(userRepository.findByEmail(any()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                service.loadUserByUsername("x"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}

