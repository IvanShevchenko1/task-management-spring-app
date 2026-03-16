package org.shevchenko.taskmanagementspringapp.repository;

import org.shevchenko.taskmanagementspringapp.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findAllByOwnerId(Long id, Pageable pageable);
}
