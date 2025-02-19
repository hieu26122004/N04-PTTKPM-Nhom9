package com.nhson.examservice.comment.repositories;

import com.nhson.examservice.comment.entities.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment,String> , CustomCommentRepository {
    List<Comment> findAllByExamId(String examId);
    List<Comment> findAllByAuthor(String author);
    int deleteAllByExamId(String examId);
    List<Comment> findAllByDeletedIsTrue();
    List<Comment> findAllByParentId(String parentId);
}
