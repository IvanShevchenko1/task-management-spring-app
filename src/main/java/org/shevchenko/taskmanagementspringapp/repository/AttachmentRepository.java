package org.shevchenko.taskmanagementspringapp.repository;

import java.util.List;
import java.util.Optional;
import org.shevchenko.taskmanagementspringapp.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAllByTaskIdOrderByUploadDateDesc(Long taskId);

    Optional<Attachment> findByIdAndTaskId(Long id, Long taskId);
}
