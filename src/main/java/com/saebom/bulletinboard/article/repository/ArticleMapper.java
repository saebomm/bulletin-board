package com.saebom.bulletinboard.article.repository;

import com.saebom.bulletinboard.article.domain.Article;
import com.saebom.bulletinboard.article.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {

    // 게시글 저장
    int insert(Article article);

    // 게시글 전체 조회
    List<ArticleListView> selectList();

    // PK로 게시글 상세 조회
    ArticleDetailView selectDetailById(@Param("id") Long id);

    // PK로 게시글 수정 뷰 조회
    ArticleEditView selectEditViewById(@Param("id") Long id);

    // PK로 게시글 권한 조회
    ArticleAuthView selectAuthById(@Param("id") Long id);

    List<MyArticleListView> selectMyListByMemberId(@Param("memberId") Long memberId);

    MyArticleDetailView selectMyDetailById(@Param("id") Long id);

    // 게시글 수정
    int update(@Param("id") Long id,
               @Param("title") String title,
               @Param("content") String content);

    // 게시글 삭제
    int deleteById(@Param("id") Long id);

    // 조회수 증가
    int increaseViewCount(@Param("id") Long id);

}