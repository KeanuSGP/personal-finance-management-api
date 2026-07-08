package com.keanusantos.personalfinancemanager.domain.category.dto.response;

public record CategoryResponseDTO(
    Long id,
    String name,
    String color,
    Long userId
) {
}
