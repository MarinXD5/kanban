package com.hivetech.kanban.service;

import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.ProjectRepository;
import com.hivetech.kanban.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final UserRepository userRepo;

    public ProjectServiceImpl(ProjectRepository projectRepo, UserRepository userRepo) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public Project createProject(Project project, User creator) {
        project.getUsers().add(creator);
        creator.getProjects().add(project);

        return projectRepo.save(project);
    }

    public Project addUserToProject(Long projectId, Long userId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getUsers().add(user);
        user.getProjects().add(project);

        return projectRepo.save(project);
    }

    public List<Project> getProjectsForUser(Long userId) {
        return projectRepo.findByUsers_Id(userId);
    }

    @Transactional
    public Project removeUserFromProject(Long projectId, Long userId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        project.getUsers().remove(user);
        user.getProjects().remove(project);

        return projectRepo.save(project);
    }

    public Set<User> getProjectUsers(Long projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        return project.getUsers();
    }
}