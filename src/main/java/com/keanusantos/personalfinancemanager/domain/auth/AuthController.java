package com.keanusantos.personalfinancemanager.domain.auth;

import com.keanusantos.personalfinancemanager.config.security.TokenService;
import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.config.security.UserDetailsServiceImpl;
import com.keanusantos.personalfinancemanager.domain.auth.request.AuthDTO;
import com.keanusantos.personalfinancemanager.domain.user.UserService;
import com.keanusantos.personalfinancemanager.domain.user.dto.request.CreateUserDTO;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/login")
    public String Login(@RequestBody @Valid AuthDTO dto) {
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(dto.name(), dto.password());
        Authentication authentication = authManager.authenticate(userAndPass);
        String token = tokenService.generateToken((UserDetailsImpl) authentication.getPrincipal());
        return token;
    }


    @PostMapping(value = "/register")
    public UserResponseDTO Register(@RequestBody @Valid CreateUserDTO dto) {
        return userService.insert(dto);
    }

    @GetMapping
    public String test() {
        return "Successful";
    }
}
