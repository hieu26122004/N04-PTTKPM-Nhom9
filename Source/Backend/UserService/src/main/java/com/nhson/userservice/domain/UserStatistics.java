package com.nhson.userservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistics {
    private int loadedMessagesBatchSize;
    private int loadedCommentsBatchSize;
    private int loadedAttemptsBatchSize;
    private int loadedExamsBatchSize;
}
