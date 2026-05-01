package com.keanusantos.personalfinancemanager.domain.auth;

import com.keanusantos.personalfinancemanager.config.security.UserDetailsImpl;
import com.keanusantos.personalfinancemanager.domain.user.User;
import com.keanusantos.personalfinancemanager.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public User getAuthenticatedUser() {

        Authentication auth =  SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserDetails) || auth.getPrincipal() == "anonymousUser") {
            throw new BusinessException("No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        return ((UserDetailsImpl) auth.getPrincipal()).getUser();
    }
}
