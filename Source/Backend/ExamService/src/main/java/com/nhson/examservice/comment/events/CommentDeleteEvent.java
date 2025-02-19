package com.nhson.examservice.comment.events;

import com.nhson.examservice.comment.entities.Comment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CommentDeleteEvent extends ApplicationEvent {
    private final Comment comment;
    public CommentDeleteEvent(Object source, Comment comment) {
        super(source);
        this.comment = comment;
    }
}
