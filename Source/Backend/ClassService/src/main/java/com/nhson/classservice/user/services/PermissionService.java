package com.nhson.classservice.user.services;

import com.nhson.classservice.user.AuthServiceUser;
import com.nhson.classservice.user.UserServiceClient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {
    private final UserServiceClient userServiceClient;

    public PermissionService(UserServiceClient userServiceClient) {
        this.userServiceClient = userServiceClient;
    }

    public boolean hasPermissionToDeleteClass(String classId, JwtAuthenticationToken authenticationToken){
        List<String> permissions = userServiceClient.getUserPermissions(authenticationToken);
        boolean isAdmin = authenticationToken.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_ADMIN"));
        boolean isTeacher = permissions.contains(classId + ".TEACHER");
        return isAdmin || isTeacher;
    }

    public boolean hasPermissionToAddMaterial(String classId,JwtAuthenticationToken authToken){
        List<String> userPermissions = userServiceClient.getUserPermissions(authToken);
        return userPermissions.contains(classId + ".TEACHER");
    }
    public boolean hasPermissionToAddAnnouncement(String classId,JwtAuthenticationToken authToken){
        List<String> userPermissions = userServiceClient.getUserPermissions(authToken);
        return userPermissions.contains(classId + ".TEACHER");
    }

    public void updateUserPermission(String userId,List<String> permission, String token){
        AuthServiceUser authServiceUser = userServiceClient.updateUserPermission(
                userId,
                permission,
                token
        );
    }
}
