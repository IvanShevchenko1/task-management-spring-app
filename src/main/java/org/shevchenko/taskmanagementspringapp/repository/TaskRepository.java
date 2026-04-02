package org.shevchenko.taskmanagementspringapp.repository;

import java.util.List;
import java.util.Optional;
import org.shevchenko.taskmanagementspringapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByProjectId(Long projectId);

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);
}
