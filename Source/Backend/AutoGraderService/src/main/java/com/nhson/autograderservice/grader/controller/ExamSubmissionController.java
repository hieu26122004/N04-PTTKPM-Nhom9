package com.nhson.autograderservice.grader.controller;

import com.nhson.autograderservice.grader.model.Attempt;
import com.nhson.autograderservice.grader.service.GradeService;
import com.nhson.autograderservice.validator.ValidSubmissionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam/submission")
@Validated
public class ExamSubmissionController {

    private final GradeService gradeService;

    public ExamSubmissionController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @PostMapping
    public ResponseEntity<Attempt> gradeExam(@ValidSubmissionRequest @RequestBody Attempt attempt, JwtAuthenticationToken authenticationToken) {
        Attempt attemptRes = gradeService.gradeAttempt(authenticationToken, attempt);
        return ResponseEntity.ok(attemptRes);
    }
}
