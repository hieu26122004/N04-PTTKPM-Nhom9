package com.nhson.autograderservice.grader.controller;

import com.nhson.autograderservice.grader.model.ExamSession;
import com.nhson.autograderservice.grader.service.ExamSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/exam-session")
public class ExamSessionController {
    private final ExamSessionService examSessionService;

    public ExamSessionController(ExamSessionService examSessionService) {
        this.examSessionService = examSessionService;
    }
    @PostMapping
    public ResponseEntity<ExamSession> saveSession(@RequestBody ExamSession examSession,JwtAuthenticationToken authenticationToken){
        ExamSession response = this.examSessionService.saveSession(examSession,authenticationToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/incomplete")
    public ResponseEntity<List<ExamSession>> getIncompleteSessionId(JwtAuthenticationToken authenticationToken){
        List<ExamSession> incompleteSessionId = examSessionService.checkIncompleteSession(authenticationToken);
        return ResponseEntity.ok(incompleteSessionId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteSession(@PathVariable String id,JwtAuthenticationToken authenticationToken){
        String userId = authenticationToken.getToken().getSubject();
        examSessionService.deleteSession(id,userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
//    @GetMapping("/{id}")
//    public ResponseEntity<ExamSession> getExamSession(@PathVariable("id") String id,JwtAuthenticationToken authenticationToken){
//        examSessionService.
//    }
}
