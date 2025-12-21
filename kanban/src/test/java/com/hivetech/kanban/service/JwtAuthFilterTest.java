package com.hivetech.kanban.util;

import com.hivetech.kanban.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    JwtUtil jwtUtil;

    @Mock
    CustomUserDetailsService userDetailsService;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    FilterChain filterChain;

    @InjectMocks
    JwtAuthFilter jwtAuthFilter;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWhenBearerTokenPresent()
            throws ServletException, IOException {

        // given
        String token = "jwt-token";
        String email = "test@test.com";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(jwtUtil.extractUsername(token))
                .thenReturn(email);

        User userDetails = new User(
                email,
                "password",
                Collections.emptyList()
        );

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userDetails);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueFilterChainWhenNoAuthorizationHeader()
            throws ServletException, IOException {

        // given
        when(request.getHeader("Authorization")).thenReturn(null);

        // when
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication())
                .isNull();

        verify(filterChain).doFilter(request, response);
    }
}