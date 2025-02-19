package com.nhson.authservice.filter;

import com.nhson.authservice.authentication.JwtAuthenticationConverter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtAuthenticationConverter converter;
    private final AuthenticationManager authenticationManager;
    public JwtAuthenticationFilter(JwtAuthenticationConverter converter, AuthenticationManager authenticationManager) {
        this.converter = converter;
        this.authenticationManager = authenticationManager;
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        List<String> excludedUrls = List.of(
                "/auth/register",
                "/auth/login",
                "/auth/forgot-password",
                "/auth/update-password",
                "/auth/reset-password"
        );

        String path = request.getRequestURI();
        return excludedUrls.contains(path);
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            Authentication authRequest = converter.convert(request);
            if (authRequest == null) {
                this.logger.info("Did not process authentication request since failed to find jwt in bearer header");

                filterChain.doFilter(request, response);
                return;
            }
            String username = authRequest.getName();
            if (!this.authenticationIsRequired(username)){
                Authentication authResult = authenticationManager.authenticate(authRequest);
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                context.setAuthentication(authResult);
                SecurityContextHolder.setContext(context);
            }
        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean authenticationIsRequired(String username){
        Authentication existAuthentication = SecurityContextHolder.getContext().getAuthentication();
        return existAuthentication!=null && existAuthentication.getName().equals(username) && existAuthentication.isAuthenticated();
    }
}