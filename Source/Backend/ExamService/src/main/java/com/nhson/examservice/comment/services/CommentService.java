package com.nhson.examservice.comment.services;

import com.nhson.examservice.comment.entities.Comment;
import com.nhson.examservice.comment.entities.CommentResponse;
import com.nhson.examservice.comment.events.CommentCreateEvent;
import com.nhson.examservice.comment.mappper.CommentMapper;
import com.nhson.examservice.comment.repositories.CommentRepository;
import com.nhson.examservice.exceptions.CommentNotFoundException;
import com.nhson.examservice.user.UserServiceClient;
import com.nhson.examservice.user.dto.UserStatistics;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final ApplicationEventPublisher applicationEventPublisher;

    public CommentService(CommentRepository commentRepository, UserServiceClient userServiceClient, ApplicationEventPublisher applicationEventPublisher) {
        this.commentRepository = commentRepository;
        this.userServiceClient = userServiceClient;
        this.applicationEventPublisher = applicationEventPublisher;
    }
    public Comment createComment(Comment comment, JwtAuthenticationToken authenticationToken){
        comment.setCreatedAt(LocalDateTime.now());
        comment.setLastUpdated(LocalDateTime.now());
        comment.setAuthor(authenticationToken.getToken().getClaimAsString("username"));
        comment.setId(new ObjectId().toHexString());
        comment.setParentId(null);
        Comment savedComment = commentRepository.save(comment);
        applicationEventPublisher.publishEvent(new CommentCreateEvent(this,savedComment));
        return savedComment;
    }
    @Transactional
    public Comment addReply(String parentId, Comment reply, JwtAuthenticationToken authenticationToken) {
        Optional<Comment> parentCommentOpt = commentRepository.findById(parentId);
        if (parentCommentOpt.isEmpty()) {
            throw new CommentNotFoundException("Parent comment not found");
        }
        Comment parent = parentCommentOpt.get();
        parent.setReplies(parent.getReplies() + 1);

        reply.setId(new ObjectId().toHexString());
        reply.setCreatedAt(LocalDateTime.now());
        reply.setLastUpdated(LocalDateTime.now());
        reply.setAuthor(authenticationToken.getToken().getClaimAsString("username"));
        reply.setParentId(parentId);

        Comment savedReply = commentRepository.save(reply);

        applicationEventPublisher.publishEvent(new CommentCreateEvent(this, savedReply));

        commentRepository.save(parent);
        return savedReply;
    }

    public List<Comment> getCommentByUser(JwtAuthenticationToken authenticationToken){
        return commentRepository.findAllByAuthor(authenticationToken.getToken().getClaimAsString("username"));
    }

    public List<Comment> getCommentByExam(String examId, String parentId,LocalDateTime lastPosition, String lastId, JwtAuthenticationToken authenticationToken){
        UserStatistics userStatistics = userServiceClient.getUserStatistics(authenticationToken);
        int limit = userStatistics.getLoadedCommentsBatchSize();
        lastPosition = (lastPosition==null) ? LocalDateTime.now() : lastPosition;
        return commentRepository.getCommentByExam(examId, parentId,lastPosition, lastId, limit);
    }
}
