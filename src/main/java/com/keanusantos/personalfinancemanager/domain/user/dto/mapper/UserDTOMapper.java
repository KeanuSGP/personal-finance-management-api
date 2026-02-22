package com.keanusantos.personalfinancemanager.domain.user.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;

public class UserDTOMapper {

    public static UserResponseDTO toResponse(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

}
