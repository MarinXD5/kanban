package com.hivetech.kanban.service;

import com.hivetech.kanban.dto.TaskEvent;
import com.hivetech.kanban.dto.TaskResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public TaskEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void publish(TaskEvent event) {
        messagingTemplate.convertAndSend("/topic/tasks", event);
    }
}