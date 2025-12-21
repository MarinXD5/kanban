package com.hivetech.kanban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.util.JsonMergePatchUtil;
import com.hivetech.kanban.util.JwtUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtUtilTest {

    JwtUtil jwtUtil = new JwtUtil();

    @Test
    void shouldGenerateAndParseToken() {
        String token = jwtUtil.generateToken("a@a.com");

        String email = jwtUtil.extractUsername(token);

        assertThat(email).isEqualTo("a@a.com");
    }

    @Test
    void shouldGenerateAndExtractUsername() {
        JwtUtil jwtUtil = new JwtUtil();

        String token = jwtUtil.generateToken("test@test.com");
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
