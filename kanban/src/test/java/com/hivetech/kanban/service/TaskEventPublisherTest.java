package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.TaskEvent;
import com.hivetech.kanban.dto.TaskResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TaskEventPublisherTest {
    @Mock
    SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    TaskEventPublisher publisher;

    @Test
    void shouldPublishEventToTopic() {
        Long projectId = 42L;
        String projectName = "Kanban Project";

        TaskResponse response = new TaskResponse(
                1L,
                "Test task",
                null,
                projectId,
                null,
                null,
                null,
                null,
                null
        );

        TaskEvent event = new TaskEvent(
                TaskEvent.Type.CREATED,
                response,
                projectId,
                projectName
        );

        publisher.publish(event);

        verify(messagingTemplate).convertAndSend("/topic/tasks", event);
    }
}
