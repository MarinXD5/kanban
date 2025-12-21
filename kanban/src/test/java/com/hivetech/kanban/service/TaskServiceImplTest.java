package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.TaskEvent;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.entity.*;
import com.hivetech.kanban.repository.ProjectRepository;
import com.hivetech.kanban.repository.TaskRepository;
import com.hivetech.kanban.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TaskServiceImplTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    TaskEventPublisher eventPublisher;

    @InjectMocks
    TaskServiceImpl taskService;

    @Mock
    ProjectRepository projectRepository;

    @Mock
    UserRepository userRepository;

    @Test
    void shouldCreateTaskAndPublishEvent() {
        Task task = new Task();
        task.setTitle("Test");
        task.setStatus(TaskStatus.TO_DO);

        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.create(task, 1L);

        assertThat(result).isNotNull();
        verify(taskRepository).save(task);
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.findById(1L);

        assertThat(result).isEqualTo(task);
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Task not found");
    }

    @Test
    void shouldUpdateTask() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setTitle("Old");
        existing.setStatus(TaskStatus.TO_DO);

        Project project = new Project();
        project.setId(1L);
        existing.setProject(project);

        Task updated = new Task();
        updated.setTitle("New");
        updated.setStatus(TaskStatus.DONE);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(existing)).thenReturn(existing);

        Task result = taskService.update(1L, updated);

        assertThat(result.getTitle()).isEqualTo("New");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);

        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldPatchOnlyProvidedFields() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setTitle("Title");
        existing.setDescription("Desc");
        existing.setStatus(TaskStatus.TO_DO);
        existing.setPriority(TaskPriority.MEDIUM);

        Project project = new Project();
        project.setId(1L);
        existing.setProject(project);

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setStatus(TaskStatus.DONE);

        ObjectNode patchNode = new ObjectMapper().createObjectNode();
        patchNode.put("status", "DONE");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(existing)).thenReturn(existing);

        Task result = taskService.patch(1L, taskRequest, patchNode);

        assertThat(result.getStatus()).isEqualTo(TaskStatus.DONE);
        assertThat(result.getTitle()).isEqualTo("Title");
        assertThat(result.getDescription()).isEqualTo("Desc");
        assertThat(result.getPriority()).isEqualTo(TaskPriority.MEDIUM);

        verify(taskRepository).save(existing);
        verify(eventPublisher).publish(any(TaskEvent.class));
    }

    @Test
    void shouldDeleteTaskAndPublishEvent() {
        Task task = new Task();
        task.setId(1L);

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L);

        verify(taskRepository).delete(task);
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldRemoveAssigneeWhenPatchContainsNull() {
        Task task = new Task();
        task.setId(1L);
        task.setAssignee(new User());

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        TaskRequest req = new TaskRequest();

        ObjectNode patchNode = new ObjectMapper().createObjectNode();
        patchNode.putNull("assigneeId");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.patch(1L, req, patchNode);

        assertThat(result.getAssignee()).isNull();
    }

    @Test
    void shouldFindTasksByStatusWhenStatusProvided() {
        when(taskRepository.findByProject_IdAndStatus(any(), any(), any()))
                .thenReturn(Page.empty());

        Page<Task> result = taskService.findAll(
                1L,
                TaskStatus.TO_DO,
                Pageable.unpaged()
        );

        assertThat(result).isNotNull();
    }

    @Test
    void shouldAssignUserWhenAssigneeIdProvided() {
        Task task = new Task();
        task.setId(1L);

        User user = new User();
        user.setId(5L);

        TaskRequest req = new TaskRequest();
        req.setAssigneeId(5L);

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        ObjectNode patchNode = new ObjectMapper().createObjectNode();
        patchNode.put("assigneeId", 5L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.patch(1L, req, patchNode);

        assertThat(result.getAssignee()).isEqualTo(user);
    }

    @Test
    void shouldThrowWhenAssigneeUserNotFound() {
        Task task = new Task();
        task.setId(1L);

        TaskRequest req = new TaskRequest();
        req.setAssigneeId(99L);

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        ObjectNode patchNode = new ObjectMapper().createObjectNode();
        patchNode.put("assigneeId", 99L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.patch(1L, req, patchNode))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void shouldReorderTasksWithinSameStatus() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.TO_DO);

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findAllByStatusOrderByOrderIndexAsc(TaskStatus.TO_DO))
                .thenReturn(new ArrayList<>(List.of(task)));

        taskService.reorderTasks(1L, TaskStatus.TO_DO, 0);

        verify(taskRepository).saveAll(any());
    }

    @Test
    void shouldReorderTasksAcrossStatuses() {
        Task task = new Task();
        task.setId(1L);
        task.setStatus(TaskStatus.TO_DO);

        Project project = new Project();
        project.setId(1L);
        task.setProject(project);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findAllByStatusOrderByOrderIndexAsc(TaskStatus.TO_DO))
                .thenReturn(new ArrayList<>());
        when(taskRepository.findAllByStatusOrderByOrderIndexAsc(TaskStatus.DONE))
                .thenReturn(new ArrayList<>());

        taskService.reorderTasks(1L, TaskStatus.DONE, 0);

        verify(taskRepository, times(2)).saveAll(any());
    }

}
