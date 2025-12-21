package com.hivetech.kanban.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hivetech.kanban.dto.TaskRequest;
import com.hivetech.kanban.util.JsonMergePatchUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JsonMergePatchUtilTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldMergePatch() throws JsonProcessingException {
        TaskRequest original = new TaskRequest();
        original.setTitle("Old");

        JsonNode patch = mapper.readTree("""
        { "title": "New" }
        """);

        TaskRequest merged =
                JsonMergePatchUtil.merge(
                        mapper,
                        original,
                        patch,
                        TaskRequest.class
                );

        assertThat(merged.getTitle()).isEqualTo("New");
    }
}
