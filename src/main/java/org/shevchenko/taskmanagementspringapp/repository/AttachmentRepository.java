package org.shevchenko.taskmanagementspringapp.repository;

import java.util.Optional;
import org.shevchenko.taskmanagementspringapp.model.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    Page<Attachment> findAllByTaskIdOrderByUploadDateDesc(Long taskId, Pageable pageable);

    Optional<Attachment> findByIdAndTaskId(Long id, Long taskId);
}
