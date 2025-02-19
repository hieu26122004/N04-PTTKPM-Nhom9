package com.nhson.authservice.security;

import com.nhson.authservice.filter.JwtAuthenticationFilter;
import com.nhson.authservice.service.CustomOidcUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class FilterChainConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomOidcUserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler successHandler;
    private final GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    public FilterChainConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CustomOidcUserService customOAuth2UserService, OAuth2AuthenticationSuccessHandler successHandler, GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.customOAuth2UserService = customOAuth2UserService;
        this.successHandler = successHandler;
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOAuth2UserService)
                                .userAuthoritiesMapper(grantedAuthoritiesMapper)
                        )
                        .successHandler(successHandler)
                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/register", "/auth/login", "/auth/forgot-password", "/auth/update-password", "/auth2","/auth/reset-password").permitAll() // Public endpoints
                        .requestMatchers("/hello").hasAuthority("ROLE_USER") // Endpoint yêu cầu quyền hạn cụ thể
                        .anyRequest().authenticated() // Các endpoint còn lại yêu cầu xác thực
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
