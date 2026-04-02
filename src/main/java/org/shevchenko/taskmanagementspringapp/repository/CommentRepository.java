package org.shevchenko.taskmanagementspringapp.repository;

import java.util.List;
import org.shevchenko.taskmanagementspringapp.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTaskIdOrderByTimestampAsc(Long taskId);
}
