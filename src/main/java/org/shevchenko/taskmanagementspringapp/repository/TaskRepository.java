package org.shevchenko.taskmanagementspringapp.repository;

import java.util.Optional;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByProjectId(Long projectId, Pageable pageable);

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
}
