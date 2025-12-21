package com.hivetech.kanban.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.dto.TaskResponse;
import com.hivetech.kanban.entity.Task;
import com.hivetech.kanban.entity.TaskStatus;
import com.hivetech.kanban.mapper.TaskMapper;
import com.hivetech.kanban.service.TaskService;
import com.hivetech.kanban.util.JsonMergePatchUtil;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ObjectMapper objectMapper;

    public TaskController(TaskService taskService, ObjectMapper objectMapper) {
        this.taskService = taskService;
        this.objectMapper = objectMapper;
    }

    // GET /api/tasks?status=IN_PROGRESS&page=0&size=10
    @GetMapping
    public Page<TaskResponse> getTasks(
            @RequestParam Long projectId,
            @RequestParam(required = false) TaskStatus status,
            Pageable pageable
    ) {
        return taskService.findAll(projectId, status, pageable)
                .map(TaskMapper::toResponse);
    }

    // GET /api/tasks/{id}
    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id) {
        return TaskMapper.toResponse(taskService.findById(id));
    }

    // POST /api/tasks
    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody TaskRequest request) {
        Task task = TaskMapper.toEntity(request);

        Task created = taskService.create(
                task,
                request.getProjectId()
        );

        return TaskMapper.toResponse(created);
    }

    // PUT /api/tasks/{id}
    @PutMapping("/{id}")
    public TaskResponse updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request
    ) {
        Task updated = TaskMapper.toEntity(request);
        return TaskMapper.toResponse(taskService.update(id, updated));
    }

    // DELETE /api/tasks/{id}
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.delete(id);
    }

    @PatchMapping(
            value = "/{id}",
            consumes = "application/merge-patch+json"
    )
    public TaskResponse patchTask(
            @PathVariable Long id,
            @RequestBody String patchJson
    ) throws Exception {

        Task existing = taskService.findById(id);
        TaskRequest current = TaskMapper.toRequest(existing);

        JsonNode patchNode = objectMapper.readTree(patchJson);

        TaskRequest patched = JsonMergePatchUtil.merge(
                objectMapper,
                current,
                patchNode,
                TaskRequest.class
        );

        return TaskMapper.toResponse(taskService.patch(id, patched, patchNode));
    }
}