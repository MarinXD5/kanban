package com.hivetech.kanban.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hivetech.kanban.dto.TaskEvent;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.Task;
import com.hivetech.kanban.entity.TaskStatus;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.mapper.TaskMapper;
import com.hivetech.kanban.repository.ProjectRepository;
import com.hivetech.kanban.repository.TaskRepository;
import com.hivetech.kanban.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskEventPublisher eventPublisher;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskEventPublisher eventPublisher,
                           ProjectRepository projectRepository,
                           UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.eventPublisher = eventPublisher;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Task create(Task task, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        task.setProject(project);

        int nextOrder = taskRepository.countByProject_IdAndStatus(
                projectId,
                task.getStatus()
        );
        task.setOrderIndex(nextOrder);

        Task saved = taskRepository.save(task);

        eventPublisher.publish(
                new TaskEvent(
                        TaskEvent.Type.CREATED,
                        TaskMapper.toResponse(saved)
                )
        );

        return saved;
    }

    @Override
    public Task update(Long id, Task updatedTask) {
        Task existing = findById(id);

        existing.setTitle(updatedTask.getTitle());
        existing.setDescription(updatedTask.getDescription());
        existing.setStatus(updatedTask.getStatus());
        existing.setPriority(updatedTask.getPriority());

        Task saved = taskRepository.save(existing);

        eventPublisher.publish(
                new TaskEvent(
                        TaskEvent.Type.UPDATED,
                        TaskMapper.toResponse(saved)
                )
        );

        return saved;
    }

    @Override
    public void delete(Long id) {
        Task existing = findById(id);
        taskRepository.delete(existing);

        eventPublisher.publish(
                new TaskEvent(
                        TaskEvent.Type.DELETED,
                        TaskMapper.toResponse(existing)
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Task> findAll(Long projectId, TaskStatus status, Pageable pageable) {
        if (status != null) {
            return taskRepository.findByProject_IdAndStatus(
                    projectId, status, pageable
            );
        }
        return taskRepository.findByProject_Id(projectId, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Task findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id " + id));
        return task;
    }

    @Override
    public Task patch(Long id, TaskRequest patched, JsonNode patchNode) {
        Task existing = findById(id);

        if (patched.getTitle() != null) {
            existing.setTitle(patched.getTitle());
        }
        if (patched.getDescription() != null) {
            existing.setDescription(patched.getDescription());
        }
        if (patched.getStatus() != null) {
            existing.setStatus(patched.getStatus());
        }
        if (patched.getPriority() != null) {
            existing.setPriority(patched.getPriority());
        }

        if (patchNode.has("assigneeId")) {
            if (patchNode.get("assigneeId").isNull()) {
                existing.setAssignee(null);
            } else {
                Long userId = patched.getAssigneeId();
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
                existing.setAssignee(user);
            }
        }

        Task saved = taskRepository.save(existing);

        eventPublisher.publish(
                new TaskEvent(
                        TaskEvent.Type.UPDATED,
                        TaskMapper.toResponse(saved)
                )
        );

        return saved;
    }

    @Transactional
    public void reorderTasks(
            Long taskId,
            TaskStatus newStatus,
            int newIndex
    ) {
        Task task = findById(taskId);
        TaskStatus oldStatus = task.getStatus();

        if (oldStatus == newStatus) {
            List<Task> tasks =
                    taskRepository.findAllByStatusOrderByOrderIndexAsc(oldStatus);

            tasks.removeIf(t -> t.getId().equals(taskId));
            tasks.add(newIndex, task);
            reindex(tasks);

            taskRepository.saveAll(tasks);
            publishUpdate(task);
            return;
        }

        List<Task> oldTasks =
                taskRepository.findAllByStatusOrderByOrderIndexAsc(oldStatus);
        oldTasks.removeIf(t -> t.getId().equals(taskId));
        reindex(oldTasks);

        List<Task> newTasks =
                taskRepository.findAllByStatusOrderByOrderIndexAsc(newStatus);
        newTasks.add(newIndex, task);
        reindex(newTasks);

        task.setStatus(newStatus);

        taskRepository.saveAll(oldTasks);
        taskRepository.saveAll(newTasks);

        publishUpdate(task);
    }

    private void reindex(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            tasks.get(i).setOrderIndex(i);
        }
    }

    private void publishUpdate(Task task) {
        eventPublisher.publish(
                new TaskEvent(
                        TaskEvent.Type.UPDATED,
                        TaskMapper.toResponse(task)
                )
        );
    }

}