package com.nhson.examservice.comment.mappper;

import com.nhson.examservice.comment.entities.Comment;
import com.nhson.examservice.comment.entities.CommentResponse;

import java.util.*;
import java.util.stream.Collectors;

public class CommentMapper {

    public static List<CommentResponse> mapCommentsToResponses(List<Comment> comments) {
        Map<String, List<Comment>> groupedByParentId = comments.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));
        return buildCommentResponse(groupedByParentId, null, new HashSet<>());
    }

    private static List<CommentResponse> buildCommentResponse(
            Map<String, List<Comment>> groupedComments,
            String parentId,
            Set<String> visited
    ) {
        List<Comment> parentComments = groupedComments.getOrDefault(parentId, Collections.emptyList());

        return parentComments.stream()
                .map(comment -> {
                    if (visited.contains(comment.getId())) {
                        return null;
                    }

                    visited.add(comment.getId());

                    CommentResponse response = new CommentResponse();
                    response.setId(comment.getId());
                    response.setAuthor(comment.getAuthor());
                    response.setExamId(comment.getExamId());
                    response.setContent(comment.isDeleted() ? "This comment has been deleted" : comment.getContent());
                    response.setCreatedAt(comment.getCreatedAt());
                    response.setLastUpdated(comment.getLastUpdated());
                    response.setDeleted(comment.isDeleted());

                    List<CommentResponse> replies = buildCommentResponse(groupedComments, comment.getId(), visited);
                    response.setReplies(replies);

                    visited.remove(comment.getId());

                    return response;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

