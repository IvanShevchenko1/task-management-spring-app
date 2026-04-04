package org.shevchenko.taskmanagementspringapp.repository;

import org.shevchenko.taskmanagementspringapp.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByTaskIdOrderByTimestampAsc(Long taskId, Pageable pageable);
}
