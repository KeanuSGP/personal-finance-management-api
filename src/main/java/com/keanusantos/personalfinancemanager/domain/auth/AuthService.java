package com.keanusantos.personalfinancemanager.domain.auth;

import com.keanusantos.personalfinancemanager.config.security.TokenService;
import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.config.security.UserDetailsServiceImpl;
import com.keanusantos.personalfinancemanager.domain.auth.mapper.AuthDTOMapper;
import com.keanusantos.personalfinancemanager.domain.auth.request.AuthDTO;
import com.keanusantos.personalfinancemanager.domain.auth.response.AuthResponseDTO;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.domain.user.dto.mapper.UserDTOMapper;
import com.keanusantos.personalfinancemanager.domain.user.dto.response.UserResponseDTO;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authManager;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserDetailsServiceImpl userDetailsImpl;

    public AuthResponseDTO login(AuthDTO credentials) {
        try {
            UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(credentials.name(), credentials.password());
            Authentication auth = authManager.authenticate(userAndPass);
            String token = tokenService.generateToken((UserDetailsImpl) auth.getPrincipal());
            User user = ((UserDetailsImpl) userDetailsImpl.loadUserByUsername(credentials.name())).getUser();
            UserResponseDTO toResponse = UserDTOMapper.toResponse(user);
            AuthResponseDTO authResponseDTO = AuthDTOMapper.toAuthResponseDTO(toResponse, token);
            return authResponseDTO;
        } catch (BadCredentialsException e) {
            throw new BusinessException("Bad credentials or user not found", HttpStatus.BAD_REQUEST);
        }
    }

    public User getAuthenticatedUser() {

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails) || auth.getPrincipal() == "anonymousUser") {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        return ((UserDetailsImpl) auth.getPrincipal()).getUser();
    }

    public String validate(String header) {
        String token = header.substring("Bearer ".length());
        return tokenService.validateToken(token);
    }
}
