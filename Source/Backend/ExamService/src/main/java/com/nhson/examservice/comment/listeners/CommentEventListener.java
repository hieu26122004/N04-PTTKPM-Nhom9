package com.nhson.examservice.comment.listeners;

import com.nhson.examservice.comment.entities.Comment;
import com.nhson.examservice.comment.events.CommentDeleteEvent;
import com.nhson.examservice.comment.repositories.CommentRepository;
import com.nhson.examservice.exam.event.ExamDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
public class CommentEventListener {
    private final CommentRepository commentRepository;
    public CommentEventListener(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }
    @EventListener
    @Transactional
    public void handleDeleteExamEvent(ExamDeletedEvent event) {
        String examId = event.getExam().getExamId();
        try {
            int deletedCount = commentRepository.deleteAllByExamId(examId);
            log.info("Successfully deleted {} comments for exam {}", deletedCount, examId);
        } catch (Exception e) {
            log.error("Error deleting comments for exam {}: {}", examId, e.getMessage(), e);
            markCommentsAsDeleted(examId);
        }
    }
    @EventListener
    public void handleDeleteCommentEvent(CommentDeleteEvent event) {
        Comment comment = event.getComment();
        try {
            commentRepository.delete(comment);
            log.info("Successfully deleted comment with ID: {}", comment.getId());
            deleteRepliesRecursively(comment);
        } catch (Exception e) {
            log.error("Error deleting comment with ID: {}", comment.getId(), e);
            comment.setDeleted(true);
            commentRepository.save(comment);
        }
    }
    @Transactional
    public void deleteRepliesRecursively(Comment parentComment) {
        List<Comment> replies = commentRepository.findAllByParentId(parentComment.getId());
        for (Comment reply : replies) {
            try {
                commentRepository.delete(reply);
                log.info("Successfully deleted reply with ID: {}", reply.getId());
                deleteRepliesRecursively(reply);
            } catch (Exception e) {
                log.error("Error deleting reply with ID: {}", reply.getId(), e);
                reply.setDeleted(true);
                commentRepository.save(reply);
            }
        }
    }


    private void markCommentsAsDeleted(String examId) {
        List<Comment> comments = commentRepository.findAllByExamId(examId);
        for (Comment comment : comments) {
            comment.setDeleted(true);
            try {
                commentRepository.save(comment);
                log.info("Marked comment with ID: {} as deleted", comment.getId());
            } catch (Exception e) {
                log.error("Error marking comment with ID: {} as deleted: {}", comment.getId(), e.getMessage(), e);
            }
        }
    }
}
