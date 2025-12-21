package com.hivetech.kanban.mapper;

import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.dto.TaskResponse;
import com.hivetech.kanban.dto.UserResponse;
import com.hivetech.kanban.entity.Task;
import com.hivetech.kanban.entity.User;

public class TaskMapper {

    private TaskMapper() {}

    public static Task toEntity(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        task.setPriority(request.getPriority());
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setProjectId(task.getProject().getId());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        if (task.getAssignee() != null) {
            User u = task.getAssignee();
            UserResponse ur = new UserResponse();
            ur.setId(u.getId());
            ur.setName(u.getName());
            ur.setEmail(u.getEmail());
            ur.setAvatarColor(u.getAvatarColor());
            response.setAssignee(ur);
        }

        return response;
    }

    public static TaskRequest toRequest(Task task) {
        TaskRequest request = new TaskRequest();
        request.setTitle(task.getTitle());
        request.setDescription(task.getDescription());
        request.setProjectId(task.getProject().getId());
        request.setStatus(task.getStatus());
        request.setPriority(task.getPriority());
        return request;
    }
}