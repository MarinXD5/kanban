package com.hivetech.kanban.repository;

import com.hivetech.kanban.entity.Task;
import com.hivetech.kanban.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    int countByProject_IdAndStatus(Long projectId, TaskStatus status);

    List<Task> findAllByStatusOrderByOrderIndexAsc(TaskStatus status);

    Page<Task> findByProject_IdAndStatus(
            Long projectId,
            TaskStatus status,
            Pageable pageable
    );

    Page<Task> findByProject_Id(Long projectId, Pageable pageable);
}