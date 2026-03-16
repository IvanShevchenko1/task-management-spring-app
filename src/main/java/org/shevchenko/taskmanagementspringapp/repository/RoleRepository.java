package org.shevchenko.taskmanagementspringapp.repository;

import java.util.Optional;
import org.shevchenko.taskmanagementspringapp.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(Role.RoleName role);
}
