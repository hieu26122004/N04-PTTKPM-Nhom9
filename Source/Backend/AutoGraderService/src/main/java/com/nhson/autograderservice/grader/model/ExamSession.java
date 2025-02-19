package com.nhson.autograderservice.grader.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class ExamSession {
    private String sessionId;
    private String userId;
    private String examId;
    private String examName;
    private List<Answer> userAnswers;
    private LocalDateTime startTime;
    private Integer totalTime;
    private Integer remainTime;
    private Long expirationTime;
    private Boolean expired;

    public boolean isExpired() {
        if (startTime == null || expirationTime == null) {
            return false;
        }
        LocalDateTime expirationDateTime = startTime.plusSeconds(expirationTime);
        return LocalDateTime.now().isAfter(expirationDateTime);
    }

}
