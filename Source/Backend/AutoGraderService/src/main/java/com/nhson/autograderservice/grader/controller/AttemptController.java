package com.nhson.autograderservice.grader.controller;

import com.nhson.autograderservice.grader.model.Attempt;
import com.nhson.autograderservice.grader.service.AttemptService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/attempts")
public class AttemptController {

    private final AttemptService attemptService;

    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @GetMapping()
    public ResponseEntity<List<Attempt>> getAllAttempts(JwtAuthenticationToken authenticationToken) {
        List<Attempt> attempts = attemptService.getAllAttemptsByUserId(authenticationToken);
        return ResponseEntity.ok(attempts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attempt> getAttemptById(JwtAuthenticationToken authenticationToken,@PathVariable("id") String id) throws AccessDeniedException {
        Attempt attempt = attemptService.getAttemptByAttemptId(authenticationToken, id);
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<Attempt> getAttemptDetailsById(JwtAuthenticationToken authenticationToken,@PathVariable("id") String id) throws AccessDeniedException {
        Attempt attempt = attemptService.getAttemptDetailsByAttemptId(authenticationToken, id);
        return ResponseEntity.ok(attempt);
    }
}
