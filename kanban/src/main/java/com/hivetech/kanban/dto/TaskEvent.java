package com.hivetech.kanban.dto;

public class TaskEvent {

    public enum Type {
        CREATED,
        UPDATED,
        DELETED
    }

    private Type type;
    private TaskResponse task;

    private Long projectId;
    private String projectName;

    public TaskEvent(Type type, TaskResponse task, Long projectId, String projectName) {
        this.type = type;
        this.task = task;
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public Type getType() {
        return type;
    }

    public TaskResponse getTask() {
        return task;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setTask(TaskResponse task) {
        this.task = task;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}