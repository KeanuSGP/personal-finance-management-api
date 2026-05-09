package com.keanusantos.personalfinancemanager.domain.auth;

import com.keanusantos.personalfinancemanager.config.security.TokenService;
import com.keanusantos.personalfinancemanager.domain.auth.request.AuthDTO;
import com.keanusantos.personalfinancemanager.domain.auth.response.AuthResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.CreateUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public AuthResponseDTO Login(@RequestBody @Valid AuthDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return response;
    }

    @PostMapping(value = "/register")
    public UserResponseDTO Register(@RequestBody @Valid CreateUserDTO dto) {
        return userService.insert(dto);
    }
}
