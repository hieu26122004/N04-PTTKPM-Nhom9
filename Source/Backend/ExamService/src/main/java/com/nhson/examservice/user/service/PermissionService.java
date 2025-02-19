package com.nhson.examservice.user.service;

import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.user.UserServiceClient;
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

    public boolean hasPermissionToDelete(String username, Exam exam, JwtAuthenticationToken authenticationToken) {
        boolean isAdmin = authenticationToken.getAuthorities().contains(new SimpleGrantedAuthority("SCOPE_ADMIN"));
        boolean isOwner = username.equals(exam.getProvider());
        boolean isTeacher = exam.getClassId() != null && userServiceClient.getUserPermission(authenticationToken)
                .contains(exam.getClassId() + ".TEACHER");
        return isAdmin || isOwner || isTeacher;
    }

    public boolean hasPermissionToCreateExam(String classId, JwtAuthenticationToken authenticationToken) {
        if (classId != null && !classId.trim().isEmpty()) {
            List<String> userPermissions = userServiceClient.getUserPermission(authenticationToken);
            return userPermissions.contains(classId.trim() + ".TEACHER");
        }
        return true;
    }
}
