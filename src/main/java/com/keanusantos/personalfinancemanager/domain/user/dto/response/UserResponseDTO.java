package com.keanusantos.personalfinancemanager.domain.user.dto.response;

public record UserResponseDTO(
        Long id,
        String name,
        String email
) {
}
