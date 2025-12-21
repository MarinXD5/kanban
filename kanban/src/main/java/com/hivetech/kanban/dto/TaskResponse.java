package com.hivetech.kanban.dto;

import com.hivetech.kanban.entity.TaskPriority;
import com.hivetech.kanban.entity.TaskStatus;
import com.hivetech.kanban.entity.User;

import java.time.Instant;

public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private TaskStatus status;
    private TaskPriority priority;
    private Instant createdAt;
    private Instant updatedAt;
    private UserResponse assignee;

    public TaskResponse(){}

    public TaskResponse(Long id, String title, String description, Long projectId, TaskStatus status, TaskPriority priority, Instant createdAt, Instant updatedAt, UserResponse assignee){
        this.id = id;
        this.title = title;
        this.description = description;
        this.projectId = projectId;
        this.status = status;
        this.priority = priority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.assignee = assignee;
    }

    // getters & setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserResponse getAssignee() {
        return assignee;
    }

    public void setAssignee(UserResponse assignee) {
        this.assignee = assignee;
    }
}