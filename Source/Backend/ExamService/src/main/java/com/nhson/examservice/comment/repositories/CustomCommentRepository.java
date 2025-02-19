package com.nhson.examservice.comment.repositories;

import com.nhson.examservice.comment.entities.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CustomCommentRepository {
    List<Comment> getCommentByExam(String examId,String parentId,LocalDateTime lastPosition,String lastId,int limit);
}
