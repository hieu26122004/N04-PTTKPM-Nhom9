package com.nhson.examservice.vote.services;

import com.nhson.examservice.vote.entities.VoteType;
import com.nhson.examservice.vote.repositories.VoteRepository;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class VoteService {
    private final VoteRepository voteRepository;

    public VoteService(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    public void upvote(String examId, JwtAuthenticationToken authenticationToken){
        voteRepository.upvoteOrDownvote(examId,authenticationToken.getName(), VoteType.UPVOTE);
    }
    public void downvote(String examId,JwtAuthenticationToken authenticationToken){
        voteRepository.upvoteOrDownvote(examId,authenticationToken.getName(), VoteType.DOWNVOTE);
    }
}
