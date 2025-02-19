package com.nhson.autograderservice.grader;

import com.nhson.autograderservice.grader.model.Attempt;
import com.nhson.autograderservice.grader.model.ExamSession;
import com.nhson.autograderservice.grader.repository.AttemptRepository;
import com.nhson.autograderservice.grader.service.ExamSessionService;
import com.nhson.autograderservice.grader.service.GradeService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpiredListener implements MessageListener {
    private final ExamSessionService examSessionService;
    private final AttemptRepository attemptRepository;
    private final GradeService gradeService;

    public RedisKeyExpiredListener(ExamSessionService examSessionService, AttemptRepository attemptRepository, GradeService gradeService) {
        this.examSessionService = examSessionService;
        this.attemptRepository = attemptRepository;
        this.gradeService = gradeService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String sessionId = new String(message.getBody());
        ExamSession expiredSession = examSessionService.getSession(sessionId);
        if (expiredSession != null) {
            Attempt attempt = new Attempt(expiredSession);
            Attempt graded = gradeService.gradeAttempt(null, attempt);
            graded.setUserId(expiredSession.getUserId());
            attemptRepository.save(graded);
            examSessionService.deleteSession(sessionId,expiredSession.getUserId());
        }
    }

}
