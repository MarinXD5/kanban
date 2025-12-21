package com.hivetech.kanban.dto;

public class TaskEvent {

    public enum Type {
        CREATED,
        UPDATED,
        DELETED
    }

    private Type type;
    private TaskResponse task;

    public TaskEvent(Type type, TaskResponse task) {
        this.type = type;
        this.task = task;
    }

    public Type getType() {
        return type;
    }

    public TaskResponse getTask() {
        return task;
    }
}