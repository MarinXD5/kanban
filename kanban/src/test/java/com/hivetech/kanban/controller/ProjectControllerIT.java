package com.hivetech.kanban.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivetech.kanban.dto.CreateProjectRequest;
import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.service.CustomUserDetailsService;
import com.hivetech.kanban.service.ProjectServiceImpl;
import com.hivetech.kanban.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProjectControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProjectServiceImpl projectService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    JwtUtil jwtUtil;

    @MockBean
    CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldCreateProject() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test");
        request.setDescription("Desc");

        User user = new User();
        user.setEmail("test@test.com");

        Project project = new Project();
        project.setName("Test");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));
        when(projectService.createProject(any(), any()))
                .thenReturn(project);

        Principal principal = () -> "test@test.com";

        mockMvc.perform(post("/api/projects")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test"));
    }

    @Test
    void shouldAddUserToProject() throws Exception {
        when(projectService.addUserToProject(1L, 2L))
                .thenReturn(new Project());

        mockMvc.perform(post("/api/projects/1/users/2"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetProjectsForUser() throws Exception {
        when(projectService.getProjectsForUser(1L))
                .thenReturn(List.of(new Project()));

        mockMvc.perform(get("/api/projects/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldRemoveUserFromProject() throws Exception {
        when(projectService.removeUserFromProject(1L, 2L))
                .thenReturn(new Project());

        mockMvc.perform(delete("/api/projects/1/users/2"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetProjectUsers() throws Exception {
        when(projectService.getProjectUsers(1L))
                .thenReturn(Set.of(new User()));

        mockMvc.perform(get("/api/projects/1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
