package com.nhson.authservice.authentication;

import com.nhson.authservice.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class JwtAuthenticationConverter implements AuthenticationConverter {
    @Autowired
    private JwtTokenProvider jwtUtil;
    @Override
    public Authentication convert(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if(header == null){
            return null;
        }else{
            if(!StringUtils.startsWithIgnoreCase(header,"Bearer")){
                return null;
            } else if (header.equalsIgnoreCase("Bearer")) {
                throw new BadCredentialsException("Empty JWT");
            }else{
                String token = header.substring(6);
                String username = jwtUtil.getUsernameFromToken(token);
                List<GrantedAuthority> authorities = jwtUtil.getAuthorities(token);
                if(username == null){
                    throw new BadCredentialsException("Invalid jwt");
                }else{
                    JwtAuthentication jwtAuthentication = new JwtAuthentication(username,"",authorities);
                    return jwtAuthentication;
                }
            }
        }
    }
}
