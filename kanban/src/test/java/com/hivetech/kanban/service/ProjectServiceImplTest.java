package com.hivetech.kanban.service;

import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.ProjectRepository;
import com.hivetech.kanban.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ProjectServiceImplTest {

    @Mock
    ProjectRepository projectRepo;

    @Mock
    UserRepository userRepo;

    @InjectMocks
    ProjectServiceImpl service;

    @Test
    void shouldCreateProject() {
        User user = new User();
        user.setProjects(new HashSet<>());

        Project project = new Project();
        project.setUsers(new HashSet<>());

        when(projectRepo.save(project)).thenReturn(project);

        Project result = service.createProject(project, user);

        assertThat(result).isNotNull();
        assertThat(project.getUsers()).contains(user);
    }

    @Test
    void shouldAddUserToProject() {
        Project project = new Project();
        project.setUsers(new HashSet<>());

        User user = new User();
        user.setProjects(new HashSet<>());

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(projectRepo.save(project)).thenReturn(project);

        Project result = service.addUserToProject(1L, 2L);

        assertThat(result.getUsers()).contains(user);
    }

    @Test
    void shouldThrowWhenProjectNotFound() {
        when(projectRepo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addUserToProject(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        Project project = new Project();
        project.setUsers(new HashSet<>());

        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
        when(userRepo.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addUserToProject(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldGetProjectsForUser() {
        when(projectRepo.findByUsers_Id(1L))
                .thenReturn(List.of(new Project()));

        var result = service.getProjectsForUser(1L);

        assertThat(result).hasSize(1);
    }

    @Test
    void shouldGetProjectUsers() {
        Project project = new Project();
        project.setUsers(new HashSet<>());

        when(projectRepo.findById(1L))
                .thenReturn(Optional.of(project));

        var users = service.getProjectUsers(1L);

        assertThat(users).isNotNull();
    }

    @Test
    void shouldThrowWhenGettingUsersForMissingProject() {
        when(projectRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getProjectUsers(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void shouldRemoveUserFromProject() {
        User user = new User();
        user.setProjects(new HashSet<>());

        Project project = new Project();
        project.setUsers(new HashSet<>());
        project.getUsers().add(user);

        user.getProjects().add(project);

        when(projectRepo.findById(1L))
                .thenReturn(Optional.of(project));
        when(userRepo.findById(2L))
                .thenReturn(Optional.of(user));
        when(projectRepo.save(project))
                .thenReturn(project);

        Project result = service.removeUserFromProject(1L, 2L);

        assertThat(result.getUsers()).doesNotContain(user);
    }

    @Test
    void shouldThrowWhenRemovingFromMissingProject() {
        when(projectRepo.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeUserFromProject(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Project not found");
    }

    @Test
    void shouldThrowWhenRemovingMissingUser() {
        Project project = new Project();
        project.setUsers(new HashSet<>());

        when(projectRepo.findById(1L))
                .thenReturn(Optional.of(project));
        when(userRepo.findById(2L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.removeUserFromProject(1L, 2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
