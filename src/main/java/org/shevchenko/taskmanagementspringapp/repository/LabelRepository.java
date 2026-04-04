package org.shevchenko.taskmanagementspringapp.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.shevchenko.taskmanagementspringapp.model.Label;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    Page<Label> findAllByUserId(Long userId, Pageable pageable);

    Optional<Label> findByIdAndUserId(Long id, Long userId);

    List<Label> findAllByIdInAndUserId(Collection<Long> id, Long userId);

    Page<Label> findAllByTasksId(Long taskId, Pageable pageable);
}
