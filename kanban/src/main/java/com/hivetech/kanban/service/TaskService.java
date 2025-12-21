package com.hivetech.kanban.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.entity.Task;
import com.hivetech.kanban.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TaskService {

    Page<Task> findAll(Long projectId, TaskStatus status, Pageable pageable);

    Task findById(Long id);

    Task create(Task task, Long projectId);

    Task update(Long id, Task updatedTask);

    void delete(Long id);

    Task patch(Long id, TaskRequest patched, JsonNode patchNode);
}
