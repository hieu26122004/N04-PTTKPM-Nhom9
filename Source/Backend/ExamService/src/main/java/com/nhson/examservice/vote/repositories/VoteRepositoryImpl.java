package com.nhson.examservice.vote.repositories;

import com.nhson.examservice.exam.entities.Exam;
import com.nhson.examservice.vote.entities.Vote;
import com.nhson.examservice.vote.entities.VoteType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class VoteRepositoryImpl implements VoteRepositoryCustom{
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Override
    public void upvoteOrDownvote(String examId, String userId, VoteType newVoteType) {
        String checkVoteSql = "SELECT * FROM vote WHERE exam_id = :examId AND user_id = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("examId", examId);
        params.put("userId", userId);

        List<Vote> existingVotes = namedParameterJdbcTemplate.query(checkVoteSql, params, (rs, rowNum) -> {
            Vote vote = new Vote();
            vote.setId(rs.getLong("id"));
            vote.setVoteType(VoteType.valueOf(rs.getString("vote_type")));
            vote.setExam(new Exam(rs.getString("exam_id")));
            return vote;
        });

        if (existingVotes.isEmpty()) {
            String insertVoteSql = "INSERT INTO vote (exam_id, user_id, vote_type) VALUES (:examId, :userId, :voteType)";
            Map<String, Object> insertParams = new HashMap<>();
            insertParams.put("examId", examId);
            insertParams.put("userId", userId);
            insertParams.put("voteType", newVoteType.name());
            namedParameterJdbcTemplate.update(insertVoteSql, insertParams);
        } else {
            Vote existingVote = existingVotes.get(0);
            if (existingVote.getVoteType() != newVoteType) {
                String updateVoteSql = "UPDATE vote SET vote_type = :voteType WHERE exam_id = :examId AND user_id = :userId";
                Map<String, Object> updateParams = new HashMap<>();
                updateParams.put("examId", examId);
                updateParams.put("userId", userId);
                updateParams.put("voteType", newVoteType.name());
                namedParameterJdbcTemplate.update(updateVoteSql, updateParams);
            }
        }
    }
}
