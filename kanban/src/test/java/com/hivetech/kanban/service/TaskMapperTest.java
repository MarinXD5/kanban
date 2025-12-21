package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.dto.TaskResponse;
import com.hivetech.kanban.dto.UserResponse;
import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.Task;
import com.hivetech.kanban.entity.TaskPriority;
import com.hivetech.kanban.entity.TaskStatus;
import com.hivetech.kanban.entity.User;
import com.hivetech.kanban.mapper.TaskMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class TaskMapperTest {

    @Test
    void shouldMapRequestToEntity() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Title");
        request.setDescription("Desc");
        request.setStatus(TaskStatus.TO_DO);
        request.setPriority(TaskPriority.HIGH);

        Task task = TaskMapper.toEntity(request);

        assertThat(task.getTitle()).isEqualTo("Title");
        assertThat(task.getDescription()).isEqualTo("Desc");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.TO_DO);
        assertThat(task.getPriority()).isEqualTo(TaskPriority.HIGH);
    }

    @Test
    void shouldMapTaskToRequest() {
        Project project = new Project();
        project.setId(1L);

        Task task = new Task();
        task.setTitle("Title");
        task.setDescription("Desc");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setPriority(TaskPriority.MEDIUM);
        task.setProject(project);

        TaskRequest request = TaskMapper.toRequest(task);

        assertThat(request.getTitle()).isEqualTo("Title");
        assertThat(request.getDescription()).isEqualTo("Desc");
        assertThat(request.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(request.getPriority()).isEqualTo(TaskPriority.MEDIUM);
        assertThat(request.getProjectId()).isEqualTo(1L);
    }

    @Test
    void shouldMapTaskToResponseWithoutAssignee() {
        Project project = new Project();
        project.setId(1L);

        Task task = new Task();
        task.setId(10L);
        task.setTitle("Task");
        task.setDescription("Desc");
        task.setStatus(TaskStatus.DONE);
        task.setPriority(TaskPriority.LOW);
        task.setCreatedAt(Instant.now());
        task.setUpdatedAt(Instant.now());
        task.setProject(project);

        TaskResponse response = TaskMapper.toResponse(task);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getTitle()).isEqualTo("Task");
        assertThat(response.getProjectId()).isEqualTo(1L);
        assertThat(response.getAssignee()).isNull();
    }

    @Test
    void shouldMapTaskToResponseWithAssignee() {
        Project project = new Project();
        project.setId(2L);

        User user = new User();
        user.setId(5L);
        user.setName("Marin");
        user.setEmail("marin@test.com");
        user.setAvatarColor("#00ff00");

        Task task = new Task();
        task.setId(20L);
        task.setTitle("Task");
        task.setProject(project);
        task.setAssignee(user);

        TaskResponse response = TaskMapper.toResponse(task);

        UserResponse assignee = response.getAssignee();

        assertThat(assignee).isNotNull();
        assertThat(assignee.getId()).isEqualTo(5L);
        assertThat(assignee.getName()).isEqualTo("Marin");
        assertThat(assignee.getEmail()).isEqualTo("marin@test.com");
        assertThat(assignee.getAvatarColor()).isEqualTo("#00ff00");
    }
}