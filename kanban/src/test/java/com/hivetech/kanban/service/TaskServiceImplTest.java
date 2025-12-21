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

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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


}
