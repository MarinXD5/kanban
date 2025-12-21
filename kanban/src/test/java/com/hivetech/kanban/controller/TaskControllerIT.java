package com.hivetech.kanban.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.entity.Project;
import com.hivetech.kanban.entity.TaskPriority;
import com.hivetech.kanban.entity.TaskStatus;
import com.hivetech.kanban.repository.ProjectRepository;
import com.hivetech.kanban.repository.TaskRepository;
import com.hivetech.kanban.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class TaskControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    TaskRepository taskRepository;

    @MockBean
    JwtUtil jwtUtil;

    private Project createProject() {
        Project project = new Project();
        project.setName("Test project");
        return projectRepository.save(project);
    }

    @Test
    void shouldCreateTask() throws Exception {
        Project project = createProject();

        TaskRequest request = new TaskRequest();
        request.setTitle("Test task");
        request.setDescription("Test description");
        request.setStatus(TaskStatus.TO_DO);
        request.setPriority(TaskPriority.HIGH);
        request.setProjectId(project.getId());

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test task"));
    }

    @Test
    void shouldGetTasks() throws Exception {
        Project project = createProject();

        mockMvc.perform(get("/api/tasks")
                        .param("projectId", project.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void shouldReturn404ForMissingTask() throws Exception {
        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateTask() throws Exception {
        Project project = createProject();

        TaskRequest request = new TaskRequest();
        request.setTitle("Original");
        request.setStatus(TaskStatus.TO_DO);
        request.setPriority(TaskPriority.MEDIUM);
        request.setProjectId(1L);

        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        request.setTitle("Updated");

        mockMvc.perform(put("/api/tasks/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated"));
    }

    @Test
    void shouldDeleteTask() throws Exception {
        Project project = createProject();

        TaskRequest request = new TaskRequest();
        request.setTitle("Delete test");
        request.setStatus(TaskStatus.TO_DO);
        request.setPriority(TaskPriority.MEDIUM);
        request.setProjectId(1L);

        String response = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/tasks/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldPatchTaskStatus() throws Exception {
        Project project = createProject();

        TaskRequest request = new TaskRequest();
        request.setTitle("Patch test");
        request.setDescription("Before patch");
        request.setStatus(TaskStatus.TO_DO);
        request.setPriority(TaskPriority.MEDIUM);
        request.setProjectId(1L);

        String createResponse = mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long id = objectMapper.readTree(createResponse).get("id").asLong();

        String patchJson = """
        {
          "status": "DONE"
        }
        """;

        mockMvc.perform(patch("/api/tasks/" + id)
                        .contentType("application/merge-patch+json")
                        .content(patchJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.title").value("Patch test"))
                .andExpect(jsonPath("$.description").value("Before patch"));
    }
}
