package com.nhson.examservice.comment.scheduler;

import com.nhson.examservice.comment.entities.Comment;
import com.nhson.examservice.comment.repositories.CommentRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Component
@EnableScheduling
@Configuration
@Slf4j
@Data
public class CommentScheduler {
    private int removeCommentRate = 1800000;
    private final CommentRepository commentRepository;
    private final TaskScheduler taskSchedulerVirtualThreads;

    public CommentScheduler(CommentRepository commentRepository, TaskScheduler taskSchedulerVirtualThreads) {
        this.commentRepository = commentRepository;
        this.taskSchedulerVirtualThreads = taskSchedulerVirtualThreads;

        this.taskSchedulerVirtualThreads.scheduleAtFixedRate(() -> {
            try {
                this.removeDeletedEmail();
            } catch (Exception e) {
                log.error("Error in scheduled task: {}", e.getMessage(), e);
            }
        }, Duration.ofMillis(removeCommentRate));
    }

    public void removeDeletedEmail() {
        List<Comment> commentsToDelete = commentRepository.findAllByDeletedIsTrue();

        if (commentsToDelete.isEmpty()) {
            log.info("No comments marked for deletion.");
        } else {
            log.info("Found {} comments marked for deletion.", commentsToDelete.size());
            for (Comment comment : commentsToDelete) {
                try {
                    commentRepository.delete(comment);
                    log.info("Deleted comment with ID: {}", comment.getId());
                } catch (Exception e) {
                    log.error("Error deleting comment with ID: {}", comment.getId(), e);
                }
            }
        }
    }
}
