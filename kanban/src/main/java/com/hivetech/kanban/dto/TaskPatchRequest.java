package com.hivetech.kanban.dto;

import com.hivetech.kanban.entity.TaskPriority;
import com.hivetech.kanban.entity.TaskStatus;

public class TaskPatchRequest {

    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskPriority getPriority() {
        return priority;
    }
}