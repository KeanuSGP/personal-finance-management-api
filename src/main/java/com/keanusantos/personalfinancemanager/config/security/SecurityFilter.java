package com.keanusantos.personalfinancemanager.config.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keanusantos.personalfinancemanager.exception.StandardError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizedHeader = request.getHeader("Authorization");
            if (authorizedHeader != null && authorizedHeader.startsWith("Bearer ")) {
                String token = authorizedHeader.substring("Bearer ".length());
                String username = tokenService.validateToken(token);
                UserDetailsImpl user = (UserDetailsImpl) userDetailsService.loadUserByUsername(username);

                Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        }catch(UsernameNotFoundException e){
            StandardError error = new StandardError(Instant.now(), HttpStatus.BAD_REQUEST, "Invalid credentials", "Invalid credentials or expired", request.getRequestURI());
            String json = objectMapper.writeValueAsString(error);
            response.getWriter().write(json);
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }catch(TokenExpiredException e){
            StandardError error = new StandardError(Instant.now(), HttpStatus.UNAUTHORIZED, e.getMessage(), e.getLocalizedMessage(), request.getRequestURI());
            String json = objectMapper.writeValueAsString(error);
            response.getWriter().write(json);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

    }

}
