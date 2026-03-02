package com.keanusantos.personalfinancemanager.domain.user.dto.response;

import com.keanusantos.personalfinancemanager.domain.role.Role;

import java.util.List;

public record UserResponseDTO(
        Long id,
        String name,
        List<Role> roles
) {
}
