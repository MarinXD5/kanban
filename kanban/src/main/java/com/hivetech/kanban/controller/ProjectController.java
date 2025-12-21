package com.hivetech.kanban.controller;

import com.hivetech.kanban.dto.CreateProjectRequest;
import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.repository.UserRepository;
import com.hivetech.kanban.service.ProjectServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectServiceImpl projectService;
    private final UserRepository userRepository;

    public ProjectController(ProjectServiceImpl projectService, UserRepository userRepository) {
        this.projectService = projectService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public Project createProject(
            @RequestBody CreateProjectRequest request,
            Principal principal
    ) {
        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());

        return projectService.createProject(project, user);
    }

    @PostMapping("/{projectId}/users/{userId}")
    public Project addUserToProject(
            @PathVariable Long projectId,
            @PathVariable Long userId) {
        return projectService.addUserToProject(projectId, userId);
    }

    @GetMapping("/user/{userId}")
    public List<Project> getProjectsForUser(@PathVariable Long userId) {
        return projectService.getProjectsForUser(userId);
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    public Project removeUserFromProject(
            @PathVariable Long projectId,
            @PathVariable Long userId
    ) {
        return projectService.removeUserFromProject(projectId, userId);
    }

    @GetMapping("/{projectId}/users")
    public Set<User> getProjectUsers(@PathVariable Long projectId){
        return projectService.getProjectUsers(projectId);
    }
}
