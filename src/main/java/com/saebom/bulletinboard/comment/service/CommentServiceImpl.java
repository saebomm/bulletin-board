package com.saebom.bulletinboard.comment.service;

import com.saebom.bulletinboard.comment.domain.Comment;
import com.saebom.bulletinboard.comment.dto.CommentAuthView;
import com.saebom.bulletinboard.comment.dto.CommentCreateForm;
import com.saebom.bulletinboard.comment.dto.CommentEditView;
import com.saebom.bulletinboard.comment.dto.CommentUpdateForm;
import com.saebom.bulletinboard.comment.dto.CommentView;
import com.saebom.bulletinboard.global.exception.CommentNotFoundException;
import com.saebom.bulletinboard.global.exception.NoPermissionException;
import com.saebom.bulletinboard.comment.repository.CommentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public Long createComment(Long articleId, Long loginMemberId, CommentCreateForm form) {

        Comment comment = Comment.createComment(articleId, loginMemberId, form.getContent());

        int inserted = commentMapper.insert(comment);
        if (inserted != 1) {
            throw new IllegalStateException("댓글 저장에 실패했습니다.");
        }

        return comment.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentView> getCommentList(Long articleId) {
        return commentMapper.selectListByArticleId(articleId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentEditView getCommentEditView(Long commentId) { return commentMapper.selectEditViewById(commentId); }

    @Override
    @Transactional(readOnly = true)
    public void validateCommentBelongsToArticle(Long commentId, Long articleId) {

        Long foundArticleId = commentMapper.selectArticleIdByCommentId(commentId);
        if (foundArticleId == null || !foundArticleId.equals(articleId)) {
            throw new IllegalArgumentException("해당 게시글의 댓글이 아닙니다.");
        }
    }

    @Override
    public void updateComment(Long commentId, Long loginMemberId, CommentUpdateForm form) {

        validateOwner(commentId, loginMemberId);

        int updated = commentMapper.update(commentId, form.getContent());
        if (updated != 1) {
            throw new IllegalStateException("댓글 수정에 실패했습니다.");
        }
    }

    @Override
    public void deleteComment(Long commentId, Long loginMemberId) {

        validateOwner(commentId, loginMemberId);

        int deleted = commentMapper.deleteById(commentId);
        if (deleted != 1) {
            throw new IllegalStateException("댓글 삭제에 실패했습니다.");
        }
    }

    private void validateOwner(Long commentId, Long loginMemberId) {

        CommentAuthView commentAuthView = commentMapper.selectAuthById(commentId);

        if (commentAuthView == null) {
            throw new CommentNotFoundException("댓글을 찾을 수 없습니다.");
        }

        if (!commentAuthView.getMemberId().equals(loginMemberId)) {
            throw new NoPermissionException("본인 댓글만 접근할 수 있습니다.");
        }
    }
}