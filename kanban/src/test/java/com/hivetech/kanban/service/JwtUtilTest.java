package com.hivetech.kanban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.util.JsonMergePatchUtil;
import com.hivetech.kanban.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class JwtUtilTest {

    @Test
    void shouldGenerateAndParseToken() {
        JwtUtil jwtUtil = new JwtUtil();

        String token = jwtUtil.generateToken(1L, "a@a.com");

        Long userId = jwtUtil.extractUserId(token);
        String email = jwtUtil.extractEmail(token);

        assertThat(userId).isEqualTo(1L);
        assertThat(email).isEqualTo("a@a.com");
    }

    @Test
    void shouldGenerateAndExtractUsername() {
        JwtUtil jwtUtil = new JwtUtil();

        String token = jwtUtil.generateToken(1L,"test@test.com");
        String email = jwtUtil.extractUsername(token);

        assertThat(email).isEqualTo("test@test.com");
    }

    @Test
    void shouldMergeJsonPatch() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        TaskRequest original = new TaskRequest();
        original.setTitle("Old");

        JsonNode patch = mapper.readTree("""
        { "title": "New" }
    """);

        TaskRequest merged = JsonMergePatchUtil.merge(
                mapper,
                original,
                patch,
                TaskRequest.class
        );

        assertThat(merged.getTitle()).isEqualTo("New");
    }
}
