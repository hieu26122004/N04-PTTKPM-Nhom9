package com.nhson.classservice.application.service;

import com.nhson.classservice.application.InviteCodeManager;
import com.nhson.classservice.application.event.AnnouncementCreateEvent;
import com.nhson.classservice.application.event.ClassDeleteEvent;
import com.nhson.classservice.application.event.MaterialCreateEvent;
import com.nhson.classservice.application.exceptions.ClassNotFoundException;
import com.nhson.classservice.application.exceptions.PermissionDeniedException;
import com.nhson.classservice.application.exceptions.UserAlreadyJoinClassException;
import com.nhson.classservice.application.model.*;
import com.nhson.classservice.application.model.Class;
import com.nhson.classservice.application.repository.*;
import com.nhson.classservice.user.services.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service quản lý chức năng liên quan đến Class (lớp học)
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ClassService {

    private final InviteCodeManager inviteCodeManager;
    private final PermissionService permissionService;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;
    private final AnnouncementRepository announcementRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SnowflakeIdService snowflakeIdService;

    /**
     * Tạo mới một ClassEntity và gán người tạo (teacher) vào participants.
     */
    public Class createClass(ClassDto dto, JwtAuthenticationToken authToken) {

        // Lấy thông tin cần thiết từ JWT
        String teacherId = authToken.getName();
        String email = Objects.toString(authToken.getToken().getClaims().get("email"), "");

        // Tạo Class
        Class classEntity = new Class();
        String classId = String.valueOf(snowflakeIdService.generateId());

        classEntity.setClassId(classId);
        classEntity.setClassName(dto.getClassName());
        classEntity.setDescription(dto.getDescription());
        classEntity.setCreatedDate(LocalDateTime.now());

        // Tạo User (teacher)
        User teacher = new User();
        teacher.setUserId(teacherId);
        teacher.setUsername(dto.getTeacherName());
        teacher.setEmail(email);

        // Lưu teacher
        User savedTeacher = userRepository.save(teacher);

        // Gán participants
        classEntity.setParticipants(List.of(savedTeacher));

        // Lưu class
        Class savedClass = classRepository.save(classEntity);

        // Gọi service bên ngoài để cập nhật quyền cho teacher
        String teacherPermission = classId + ".TEACHER";
        permissionService.updateUserPermission(teacherId,
                List.of(teacherPermission),
                authToken.getToken().getTokenValue());

        return savedClass;
    }

    /**
     * Sinh link mời tham gia lớp học, có chứa inviteToken.
     */
    public String generateInviteLink(String classId) throws Exception {
        String inviteToken = inviteCodeManager.generateInviteToken(classId);
        String encodedToken = URLEncoder.encode(inviteToken, StandardCharsets.UTF_8);
        return "http://localhost:8088/class/invite?invite-token=" + encodedToken;
    }

    /**
     * Người dùng tham gia lớp học qua inviteToken.
     */
    public String joinClass(String inviteToken, JwtAuthenticationToken authToken, String username) throws Exception {
        // Giải mã token để lấy classId
        String classId = inviteCodeManager.decodeInviteToken(inviteToken);

        // Tìm lớp học
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Class not found"));

        // Buộc Hibernate tải participants (tránh lazy loading exception)
        Hibernate.initialize(classEntity.getParticipants());

        // Lấy thông tin user từ token
        String userId = authToken.getName();
        String email = Objects.toString(authToken.getTokenAttributes().get("email"), "");

        // Kiểm tra xem người dùng đã tham gia lớp chưa
        boolean alreadyJoined = classEntity.getParticipants().stream()
                .anyMatch(u -> u.getUserId().equals(userId));
        if (alreadyJoined) {
            throw new UserAlreadyJoinClassException("User already joined the class");
        }

        // Cập nhật quyền STUDENT cho user
        String studentPermission = classId + ".STUDENT";
        permissionService.updateUserPermission(
                userId,
                List.of(studentPermission),
                authToken.getToken().getTokenValue()
        );

        // Tạo User mới và lưu
        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setUsername(username);
        newUser.setEmail(email);
        userRepository.save(newUser);

        // Thêm user vào participants
        classEntity.getParticipants().add(newUser);
        classRepository.save(classEntity);

        // Trả về link redirect hoặc tuỳ ý
        return "http://localhost:4200/class/" + classId;
    }

    /**
     * Lấy chi tiết một ClassEntity. Người lấy phải là participant của class.
     */
    public Class getClazz(String classId, JwtAuthenticationToken authToken) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ClassNotFoundException("Class not found"));

        String userId = authToken.getName();

        // Force load collections
        Hibernate.initialize(classEntity.getParticipants());
        Hibernate.initialize(classEntity.getAnnouncements());
        Hibernate.initialize(classEntity.getMaterials());

        // Kiểm tra xem user có nằm trong participants không
        boolean isParticipant = classEntity.getParticipants()
                .stream()
                .anyMatch(u -> u.getUserId().equals(userId));
        if (!isParticipant) {
            throw new PermissionDeniedException("User is not a participant of the class");
        }
        return classEntity;
    }

    /**
     * Lấy tất cả lớp học mà user đang tham gia.
     */
    public List<ClassResponse> getAllClasses(JwtAuthenticationToken authToken) {
        String userId = authToken.getName();
        List<Class> classes = classRepository.findAllByParticipants_UserId(userId);
        return classes.stream()
                .map(c -> new ClassResponse(
                        c.getClassId(),
                        c.getClassName(),
                        c.getDescription(),
                        c.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }


    /**
     * Xoá ClassEntity. Chỉ TEACHER hoặc ADMIN mới có quyền xoá.
     */
    public void deleteClass(String classId, JwtAuthenticationToken authToken){
        // Lấy quyền user
        boolean hasPermission = permissionService.hasPermissionToDeleteClass(classId,authToken);

        if (hasPermission) {
            classRepository.deleteById(classId);
            eventPublisher.publishEvent(new ClassDeleteEvent(this, classId));
        } else {
            throw new PermissionDeniedException("User is not a teacher of the class");
        }
    }

    /**
     * Thêm Material vào lớp. Chỉ TEACHER mới có quyền.
     */
    public Material addMaterialToClass(String classId, Material material, JwtAuthenticationToken authToken){
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ClassNotFoundException("Class not found"));

        if(!permissionService.hasPermissionToAddMaterial(classId,authToken)) {
            throw new PermissionDeniedException("User is not a teacher of the class");
        }

        // Tạo Id và gán Material vào ClassEntity
        String materialId = String.valueOf(snowflakeIdService.generateId());
        material.setMaterialId(materialId);
        material.setAClass(classEntity);

        Material savedMaterial = materialRepository.save(material);
        classEntity.getMaterials().add(savedMaterial);
        classRepository.save(classEntity);

        eventPublisher.publishEvent(new MaterialCreateEvent(this, savedMaterial.getMaterialId(), classEntity));
        return savedMaterial;
    }

    /**
     * Thêm Announcement vào lớp. Chỉ TEACHER mới có quyền.
     */
    public Announcement addAnnouncementToClass(String classId, Announcement announcement, JwtAuthenticationToken authToken){

        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ClassNotFoundException("Class not found"));

        if(!permissionService.hasPermissionToAddAnnouncement(classId,authToken)){
            throw new IllegalArgumentException("User is not a teacher of the class");
        }

        // Tạo ID và gán Announcement
        String announcementId = String.valueOf(snowflakeIdService.generateId());
        announcement.setAnnouncementId(announcementId);
        announcement.setClazz(classEntity);

        Announcement savedAnnouncement = announcementRepository.save(announcement);
        classRepository.save(classEntity);

        eventPublisher.publishEvent(new AnnouncementCreateEvent(this, savedAnnouncement.getAnnouncementId(),classEntity));
        return savedAnnouncement;
    }

}
