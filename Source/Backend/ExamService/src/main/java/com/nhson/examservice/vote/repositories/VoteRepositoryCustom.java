package com.nhson.examservice.vote.repositories;

import com.nhson.examservice.vote.entities.VoteType;

public interface VoteRepositoryCustom{

    void upvoteOrDownvote(String examId, String userId, VoteType newVoteType);
}
