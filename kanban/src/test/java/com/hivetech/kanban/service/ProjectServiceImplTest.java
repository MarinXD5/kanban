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

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
}
