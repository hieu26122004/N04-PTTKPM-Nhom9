package com.nhson.examservice.vote.event;

import com.nhson.examservice.vote.entities.VoteType;

public record ExamVoteEvent(String examId, VoteType vote) {
}
