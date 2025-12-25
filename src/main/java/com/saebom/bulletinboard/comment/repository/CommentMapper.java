package com.saebom.bulletinboard.comment.repository;

import com.saebom.bulletinboard.comment.domain.Comment;
import com.saebom.bulletinboard.comment.dto.CommentAuthView;
import com.saebom.bulletinboard.comment.dto.CommentEditView;
import com.saebom.bulletinboard.comment.dto.CommentView;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 저장
    int insert(Comment comment);

    // 특정 게시글의 댓글 조회
    List<CommentView> selectListByArticleId(@Param("articleId") Long articleId);
    CommentEditView selectEditViewById(@Param("id") Long id);

    // 특정 게시글의 댓글 조회
    CommentAuthView selectAuthById(@Param("id") Long id);

    // 게시글에 속한 댓글인지 조회
    Long selectArticleIdByCommentId(@Param("id") Long id);

    // 댓글 수정
    int update(@Param("id") Long id,
               @Param("content") String content);

    // 댓글 삭제
    int deleteById(@Param("id") Long id);

}