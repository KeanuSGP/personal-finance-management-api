package com.keanusantos.personalfinancemanager.domain.role;

import com.keanusantos.personalfinancemanager.domain.role.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(RoleName name);
}
