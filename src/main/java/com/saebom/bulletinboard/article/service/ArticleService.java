package com.saebom.bulletinboard.article.service;

import com.saebom.bulletinboard.article.dto.*;

import java.util.List;

public interface ArticleService {

    Long createArticle(Long loginMemberId, ArticleCreateForm form);

    List<ArticleListView> getArticleList();
    ArticleDetailView getArticleDetail(Long articleId);
    ArticleEditView getArticleEditView(Long articleId);

    List<MyArticleListView> getMyArticleList(Long memberId);
    MyArticleDetailView getMyArticleDetail(Long id);

    void updateArticle(Long articleId, Long loginMemberId, ArticleUpdateForm form);
    void deleteArticle(Long articleId, Long loginMemberId);

    void increaseViewCount(Long articleId);

}