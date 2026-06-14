package com.example.buildstore.user.repository;

import com.example.buildstore.user.model.Role;
import com.example.buildstore.user.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
