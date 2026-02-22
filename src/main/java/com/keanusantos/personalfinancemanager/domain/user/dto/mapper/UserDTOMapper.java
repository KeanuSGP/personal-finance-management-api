package com.keanusantos.personalfinancemanager.domain.user.dto.mapper;

import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.CreateUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.summary.UserSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;

public class UserDTOMapper {

    public static UserResponseDTO toResponse(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserSummaryDTO toSummary(User user){
        return new UserSummaryDTO(
                user.getId(),
                user.getName()
        );
    }

    public static User toEntity(CreateUserDTO dto){
        return new User(
                null,
                dto.name(),
                dto.email(),
                dto.password()
        );
    }

}
