package com.hivetech.kanban.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivetech.kanban.dto.UpdateUserRequest;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.service.UserServiceImpl;
import com.hivetech.kanban.util.JwtAuthFilter;
import com.hivetech.kanban.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserServiceImpl userService;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    JwtAuthFilter jwtAuthFilter;

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(new User()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("new@test.com");

        when(userService.updateUser(eq(1L), any()))
                .thenReturn(new User());

        mockMvc.perform(put("/api/users/change/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
