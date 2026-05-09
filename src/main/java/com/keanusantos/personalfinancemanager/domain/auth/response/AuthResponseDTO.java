package com.keanusantos.personalfinancemanager.domain.auth.response;

import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthResponseDTO(
        @NotNull UserResponseDTO user,
        @NotBlank String token
) {
}
