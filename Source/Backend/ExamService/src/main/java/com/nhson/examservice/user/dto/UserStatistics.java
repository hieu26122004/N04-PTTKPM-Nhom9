package com.nhson.examservice.user.dto;

import lombok.Data;

@Data
public class UserStatistics {
    private int loadedMessagesBatchSize;
    private int loadedCommentsBatchSize;
    private int loadedAttemptsBatchSize;
    private int loadedExamsBatchSize;
}
