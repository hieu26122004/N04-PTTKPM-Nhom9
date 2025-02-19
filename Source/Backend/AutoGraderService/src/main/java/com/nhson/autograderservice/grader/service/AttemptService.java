package com.nhson.autograderservice.grader.service;

import com.nhson.autograderservice.exceptions.PermissionDeniedException;
import com.nhson.autograderservice.grader.model.Attempt;
import com.nhson.autograderservice.grader.repository.AttemptRepository;
import org.hibernate.Hibernate;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class AttemptService {

    private final AttemptRepository attemptRepository;

    public AttemptService(AttemptRepository attemptRepository) {
        this.attemptRepository = attemptRepository;
    }

    public List<Attempt> getAllAttemptsByUserId(JwtAuthenticationToken authenticationToken) {
        String userId = authenticationToken.getToken().getSubject();
        List<Attempt> attempts = attemptRepository.findAllByUserId(userId);
        attempts.forEach(attempt -> attempt.setUserAnswer(null));
        return attempts;
    }

    public Attempt getAttemptByAttemptId(JwtAuthenticationToken authenticationToken,String attemptId) throws AccessDeniedException {
        Attempt attempt = attemptRepository.findById(attemptId).orElse(null);
        String userId = authenticationToken.getToken().getSubject();
        if(attempt != null){
            if(attempt.getUserId().equals(userId)){
                attempt.setUserAnswer(null);
                return attempt;
            }else{
                throw new PermissionDeniedException("You don't have permission to access this resource.");
            }
        }
        return null;
    }

    @Transactional
    public Attempt getAttemptDetailsByAttemptId(JwtAuthenticationToken authenticationToken,String attemptId) throws AccessDeniedException {
        Attempt attempt = attemptRepository.findById(attemptId).orElse(null);
        String userId = authenticationToken.getToken().getSubject();
        if(attempt != null){
            if(attempt.getUserId().equals(userId)){
                Hibernate.initialize(attempt.getUserAnswer());
                return attempt;
            }else{
                throw new PermissionDeniedException("You don't have permission to access this resource.");
            }
        }
        return null;
    }
}
