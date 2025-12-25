package com.saebom.bulletinboard.comment.service;

import com.saebom.bulletinboard.comment.dto.CommentCreateForm;
import com.saebom.bulletinboard.comment.dto.CommentEditView;
import com.saebom.bulletinboard.comment.dto.CommentUpdateForm;
import com.saebom.bulletinboard.comment.dto.CommentView;

import java.util.List;

public interface CommentService {

    Long createComment(Long articleId, Long loginMemberId, CommentCreateForm form);

    List<CommentView> getCommentList(Long articleId);
    CommentEditView getCommentEditView(Long commentId);

    void validateCommentBelongsToArticle(Long commentId, Long articleId);

    void updateComment(Long commentId, Long loginMemberId, CommentUpdateForm form);
    void deleteComment(Long commentId, Long loginMemberId);
}