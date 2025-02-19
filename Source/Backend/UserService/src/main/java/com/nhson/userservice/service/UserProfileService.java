package com.nhson.userservice.service;

import com.nhson.userservice.domain.User;
import com.nhson.userservice.domain.UserStatistics;
import com.nhson.userservice.repository.UserProfileRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Cacheable(value = "profile", key = "#userId != null ? #userId : #authenticationToken.name")
    public User getProfile(String userId, JwtAuthenticationToken authenticationToken) {
        User user;
        if (userId == null) {
            String userIdFromToken = authenticationToken.getName();
            user = userProfileRepository.findById(userIdFromToken)
                    .orElseThrow(() -> new IllegalArgumentException("Something went wrong during get profile"));
            Jwt token = authenticationToken.getToken();
            List<String> scopes = token.getClaim("scope");
            user.setRoles(scopes);
            user.setFriends(null);
        } else {
            user = userProfileRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            user.setPreferences(null);
            user.setSecuritySettings(null);
            user.setFriends(null);
        }
        return user;
    }

    public UserStatistics getUserStatistics(JwtAuthenticationToken authenticationToken) {
        User user = this.getProfile(null, authenticationToken);
        return user.getUserStatistics();
    }
}
