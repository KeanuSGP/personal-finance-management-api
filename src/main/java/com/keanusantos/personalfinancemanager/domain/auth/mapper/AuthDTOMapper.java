package com.keanusantos.personalfinancemanager.domain.auth.mapper;

import com.keanusantos.personalfinancemanager.domain.auth.response.AuthResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;


public class AuthDTOMapper {

    public static AuthResponseDTO toAuthResponseDTO(UserResponseDTO user, String token) {
        return new AuthResponseDTO(
                user, token
        );
    }
}
