package org.shevchenko.taskmanagementspringapp.repository;

import java.util.Optional;
import org.shevchenko.taskmanagementspringapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
