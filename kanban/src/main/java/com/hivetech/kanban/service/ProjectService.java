package com.hivetech.kanban.service;

import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.User;

import java.util.List;
import java.util.Set;

public interface ProjectService {
    Project createProject(Project project, User user);

    Project addUserToProject(Long projectId, Long userId);

    List<Project> getProjectsForUser(Long userId);

    Project removeUserFromProject(Long projectId, Long userId);

    Set<User> getProjectUsers(Long projectId);
}
