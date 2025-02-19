package com.nhson.authservice.security;

import com.nhson.authservice.authentication.JwtAuthenticationConverter;
import com.nhson.authservice.authentication.JwtAuthenticationProvider;
import com.nhson.authservice.filter.JwtAuthenticationFilter;
import com.nhson.authservice.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder noOpPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(userRepository);
        return new ProviderManager(daoAuthenticationProvider,jwtAuthenticationProvider);
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtAuthenticationConverter jwtAuthenticationConverter) {
        return new JwtAuthenticationFilter(jwtAuthenticationConverter, authenticationManager);
    }
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return authorities -> authorities.stream()
                .map(authority -> {
                    String roleName = authority.getAuthority();
                    if (authority instanceof OidcUserAuthority) {
                        if (roleName.startsWith("OIDC_")) {
                            roleName = roleName.substring(5);
                        }
                        return new SimpleGrantedAuthority(roleName);
                    }
                    else if (roleName.startsWith("SCOPE_")) {
                        roleName = roleName.substring(6);
                        if (roleName.startsWith("https://www.googleapis.com/auth/")) {
                            roleName = roleName.replace("https://www.googleapis.com/auth/", "googleapis.auth.");
                        }
                        return new SimpleGrantedAuthority(roleName);
                    } else if (roleName.startsWith("PERMISSION_")) {
                        roleName = roleName.substring(11);
                        return new SimpleGrantedAuthority(roleName);
                    }
                    return authority;
                })
                .collect(Collectors.toSet());
    }
}

