package com.keanusantos.personalfinancemanager.domain.auth.request;

import jakarta.validation.constraints.NotEmpty;

public record AuthDTO(
        @NotEmpty String name,
        @NotEmpty String password
) {
}
