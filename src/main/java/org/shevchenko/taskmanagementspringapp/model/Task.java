package org.shevchenko.taskmanagementspringapp.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLDelete(sql = "UPDATE tasks SET is_deleted = true WHERE task_id=?")
@SQLRestriction("is_deleted = false")
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        NOT_STARTED, IN_PROGRESS, COMPLETED
    }

}
