package org.shevchenko.taskmanagementspringapp.repository;

import org.shevchenko.taskmanagementspringapp.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
