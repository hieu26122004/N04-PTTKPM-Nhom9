package com.nhson.examservice.comment.repositories;

import com.nhson.examservice.comment.entities.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CommentRepositoryImpl implements CustomCommentRepository{
    private final MongoTemplate mongoTemplate;
    public CommentRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    @Override
    public List<Comment> getCommentByExam(String examId, String parentId,LocalDateTime lastPosition, String lastId, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where("examId").is(examId));
        if (lastPosition != null) {
            if (lastId == null) {
                query.addCriteria(Criteria.where("lastUpdated").lt(lastPosition));
            } else {
                Criteria timeCriteria = Criteria.where("lastUpdated").lt(lastPosition);
                Criteria sameTimeCriteria = new Criteria().andOperator(
                        Criteria.where("lastUpdated").is(lastPosition),
                        Criteria.where("id").lt(lastId)
                );
                query.addCriteria(new Criteria().orOperator(timeCriteria, sameTimeCriteria));
            }
        }
        if (parentId == null) {
            query.addCriteria(Criteria.where("parentId").exists(false));
        } else {
            query.addCriteria(Criteria.where("parentId").is(parentId));
        }
        query.with(Sort.by(Sort.Direction.DESC, "lastUpdated").and(Sort.by(Sort.Direction.DESC, "id")));
        query.limit(limit);

        return mongoTemplate.find(query, Comment.class);
    }


}
