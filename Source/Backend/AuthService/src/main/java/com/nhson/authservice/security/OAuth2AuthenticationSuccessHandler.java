package com.nhson.authservice.security;

import com.nhson.authservice.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtTokenProvider tokenProvider;
    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        if(authentication instanceof OAuth2AuthenticationToken oAuth2Authentication){
            String userId = oAuth2Authentication.getName();
            OAuth2User oAuth2User = oAuth2Authentication.getPrincipal();
            String username = oAuth2User.getAttribute("name");
            String jwtToken = tokenProvider.generateToken(oAuth2User);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + jwtToken + "\", \"username\": \"" + username + "\"}");
        }
    }
}
