package com.keanusantos.personalfinancemanager.domain.auth;

import com.keanusantos.personalfinancemanager.config.security.TokenService;
import com.keanusantos.personalfinancemanager.domain.auth.request.AuthDTO;
import com.keanusantos.personalfinancemanager.domain.auth.response.AuthResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.CreateUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

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
    public AuthResponseDTO login(@RequestBody @Valid AuthDTO dto) {
        AuthResponseDTO response = authService.login(dto);
        return response;
    }

    @PostMapping(value = "/register")
    public UserResponseDTO register(@RequestBody @Valid CreateUserDTO dto) {
        return userService.insert(dto);
    }

    @PostMapping(value= "/validate")
    public String validate(@RequestHeader("Authorization") String header) {
        return authService.validate(header);
    }
}
