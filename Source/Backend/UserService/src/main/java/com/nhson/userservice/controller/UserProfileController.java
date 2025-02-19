package com.nhson.userservice.controller;

import com.nhson.userservice.domain.User;
import com.nhson.userservice.domain.UserStatistics;
import com.nhson.userservice.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<User> getCurrentUserProfile(JwtAuthenticationToken authenticationToken){
        User profile = userProfileService.getProfile(null,authenticationToken);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserProfile(@PathVariable(name = "id",required = false) String userId,JwtAuthenticationToken authenticationToken){
        User profile = userProfileService.getProfile(userId,authenticationToken);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/statistics")
    public ResponseEntity<UserStatistics> getUserStatistics(JwtAuthenticationToken authenticationToken){
        UserStatistics userStatistics = userProfileService.getUserStatistics(authenticationToken);
        return ResponseEntity.ok(userStatistics);
    }

}
