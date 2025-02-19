package com.nhson.classservice.application.controller;

import com.nhson.classservice.application.model.*;
import com.nhson.classservice.application.model.Class;
import com.nhson.classservice.application.service.ClassService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/class")
public class ClassController {
    private final ClassService classService;
    public ClassController(ClassService classService) {
        this.classService = classService;
    }
    @PostMapping("/create")
    public ResponseEntity<Class> createClass(@RequestBody ClassDto classDto, JwtAuthenticationToken authenticationToken) throws Exception {
        Class clazz = classService.createClass(classDto,authenticationToken);
        return ResponseEntity.ok(clazz);
    }
    @GetMapping("/invite-link")
    public ResponseEntity<String> getInviteLink(@RequestParam("class-id") String classId) throws Exception {
        String inviteLink = classService.generateInviteLink(classId);
        return ResponseEntity.ok(inviteLink);
    }
    @GetMapping("/invite")
    public ResponseEntity<String> joinClass(
            @RequestParam("invite-token") String inviteToken,
            @RequestParam("username") String username,
            JwtAuthenticationToken authenticationToken) throws Exception {

        String classLink = classService.joinClass(inviteToken, authenticationToken, username);
        return ResponseEntity.ok(classLink);
    }
    @GetMapping("/{classId}")
    public ResponseEntity<Class> getClass(@PathVariable("classId") String classId, JwtAuthenticationToken authenticationToken) throws Exception {
        Class clazz = classService.getClazz(classId,authenticationToken);
        return ResponseEntity.ok(clazz);
    }
    @DeleteMapping("/{classId}")
    public ResponseEntity<Void> deleteClass(@PathVariable("classId") String classId, JwtAuthenticationToken authenticationToken) throws Exception {
        classService.deleteClass(classId,authenticationToken);
        return ResponseEntity.noContent().build();
    }
    @GetMapping()
    public ResponseEntity<List<ClassResponse>> getAllClasses(JwtAuthenticationToken authenticationToken) throws Exception {
        List<ClassResponse> classes = classService.getAllClasses(authenticationToken);
        return ResponseEntity.ok(classes);
    }
    @PostMapping("/{classId}/material")
    public ResponseEntity<Material> addMaterialToClass(@PathVariable("classId") String classId, @RequestBody Material material, JwtAuthenticationToken authenticationToken) throws Exception {
        Material newMaterial = classService.addMaterialToClass(classId, material, authenticationToken);
        return ResponseEntity.ok(newMaterial);
    }
    @PostMapping("/{classId}/announcement")
    public ResponseEntity<Announcement> addAnnouncementToClass(@PathVariable("classId") String classId, @RequestBody Announcement announcement, JwtAuthenticationToken authenticationToken){
        Announcement newAnnouncement = classService.addAnnouncementToClass(classId,announcement,authenticationToken);
        return ResponseEntity.ok(newAnnouncement);
    }

}
