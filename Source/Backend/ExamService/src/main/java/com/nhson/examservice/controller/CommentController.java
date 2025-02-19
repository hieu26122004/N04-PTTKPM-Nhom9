package com.nhson.examservice.controller;

import com.nhson.examservice.comment.entities.Comment;
import com.nhson.examservice.comment.entities.CommentResponse;
import com.nhson.examservice.comment.services.CommentService;
import com.nhson.examservice.exam.entities.Exam;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @PostMapping("")
    public ResponseEntity<Comment> addComment(@RequestBody Comment comment, JwtAuthenticationToken authenticationToken){
        Comment commentRes = commentService.createComment(comment,authenticationToken);
        return ResponseEntity.ok(commentRes);
    }
    @PostMapping("/reply/{id}")
    public ResponseEntity<Comment> replyComment(@PathVariable("id")String parentId,@RequestBody Comment comment,JwtAuthenticationToken authenticationToken){
        Comment commentRes = commentService.addReply(parentId,comment,authenticationToken);
        return ResponseEntity.ok(commentRes);
    }
    @GetMapping("/user")
    public ResponseEntity<List<Comment>> getCommentByUser(JwtAuthenticationToken authenticationToken){
        List<Comment> comments = commentService.getCommentByUser(authenticationToken);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/exam/{id}")
    public ResponseEntity<List<Comment>> getCommentsByUser(@PathVariable("id")String examId,@RequestParam(value = "parentId",required = false) String parentId,@RequestParam(value = "lastPosition",required = false) LocalDateTime lastPosition, @RequestParam(value = "lastId", required = false) String lastId, JwtAuthenticationToken authenticationToken){
        List<Comment> comments = commentService.getCommentByExam(examId,parentId,lastPosition,lastId,authenticationToken);
        return ResponseEntity.ok(comments);
    }

}
