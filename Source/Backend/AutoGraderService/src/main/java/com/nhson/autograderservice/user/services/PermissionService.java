package com.nhson.autograderservice.user.services;

import com.nhson.autograderservice.user.UserServiceClient;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    private final UserServiceClient userServiceClient;

    public PermissionService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public boolean hasPermissionToSubmit(String clazzId, JwtAuthenticationToken authenticationToken){
        List<String> permissions = userServiceClient.getUserPermission(authenticationToken);
        return permissions.contains(clazzId + ".STUDENT") || permissions.contains(clazzId + ".TEACHER");
    }
}
