package com.hivetech.kanban.repository;

import com.hivetech.kanban.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByUsers_Id(Long userId);
}
