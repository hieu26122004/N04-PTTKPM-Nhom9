package com.nhson.autograderservice.grader.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhson.autograderservice.grader.model.ExamSession;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ExamSessionService {
    private static final String SESSION_KEY_PREFIX = "exam_session:";
    private static final String USER_SESSIONS_KEY_PREFIX = "user_sessions:";
    private static final String SESSION_ID_COUNTER_KEY = "sessionIdCounter";

    private final RedisTemplate<String, ExamSession> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    public ExamSessionService(RedisTemplate<String, ExamSession> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public String createSessionId() {
        return String.valueOf(Objects.requireNonNull(
                redisTemplate.opsForValue().increment(SESSION_ID_COUNTER_KEY, 1)
        ));
    }

    public ExamSession saveSession(ExamSession examSession, JwtAuthenticationToken authenticationToken) {
        String userId = authenticationToken.getToken().getSubject();
        String sessionId = examSession.getSessionId();

        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = createSessionId();
            examSession.setSessionId(sessionId);
            examSession.setUserId(userId);
            long ttl = examSession.getExpirationTime();
            if (ttl > 0) {
                redisTemplate.opsForValue().set(
                        SESSION_KEY_PREFIX + sessionId,
                        examSession,
                        ttl,
                        TimeUnit.SECONDS
                );
                stringRedisTemplate.opsForSet().add(USER_SESSIONS_KEY_PREFIX + userId, sessionId);
            } else {
                throw new IllegalArgumentException("Session expiration time is invalid");
            }
        } else {
            ExamSession existingSession = getSession(sessionId);
            if (existingSession != null) {
                existingSession.setUserAnswers(examSession.getUserAnswers());
                existingSession.setRemainTime(examSession.getRemainTime());
                redisTemplate.opsForValue().set(SESSION_KEY_PREFIX + sessionId, existingSession);
            }
        }
        return examSession;
    }

    public List<ExamSession> checkIncompleteSession(JwtAuthenticationToken authenticationToken) {
        String userId = authenticationToken.getToken().getSubject();
        return getIncompleteSessions(userId);
    }

    public Set<String> getIncompleteSessionId(JwtAuthenticationToken authenticationToken){
        String userId = authenticationToken.getToken().getSubject();
        Set<String> sessionIds = stringRedisTemplate.opsForSet().members(
                USER_SESSIONS_KEY_PREFIX + userId
        );
        return  sessionIds;
    }

    public List<ExamSession> getIncompleteSessions(String userId) {
        Set<String> sessionIds = stringRedisTemplate.opsForSet().members(
                USER_SESSIONS_KEY_PREFIX + userId
        );
        if (sessionIds == null) return Collections.emptyList();

        List<ExamSession> incompleteSessions = new ArrayList<>();
        for (String sessionId : sessionIds) {
            ExamSession session = getSession(sessionId);
            if (session != null && !session.isExpired()) {
                incompleteSessions.add(session);
            } else {
                stringRedisTemplate.opsForSet().remove(
                        USER_SESSIONS_KEY_PREFIX + userId,
                        sessionId
                );
            }
        }
        return incompleteSessions;
    }

    public ExamSession getSession(String sessionId) {
        Object value = redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + sessionId);

        if (value instanceof LinkedHashMap) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.convertValue(value, ExamSession.class);
        }

        return (ExamSession) value;
    }


    public void deleteSession(String sessionId, String userId) {
        redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
        stringRedisTemplate.opsForSet().remove(
                USER_SESSIONS_KEY_PREFIX + userId,
                sessionId
        );
    }

//    private long calculateRemainingTTL(Long expirationTime) {
//        if (expirationTime == null) return 0;
//        long now = System.currentTimeMillis();
//        return (expirationTime - now) / 1000;
//    }
}