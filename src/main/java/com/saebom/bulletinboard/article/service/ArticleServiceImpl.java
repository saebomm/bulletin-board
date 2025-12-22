package com.saebom.bulletinboard.article.service;

import com.saebom.bulletinboard.article.domain.Article;
import com.saebom.bulletinboard.article.dto.*;
import com.saebom.bulletinboard.global.exception.ArticleNotFoundException;
import com.saebom.bulletinboard.global.exception.NoPermissionException;
import com.saebom.bulletinboard.article.repository.ArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper) {
        this.articleMapper = articleMapper;
    }

    @Override
    public Long createArticle(Long loginMemberId, ArticleCreateForm form) {

        Article article = Article.createArticle(loginMemberId, form.getTitle(), form.getContent());

        int inserted = articleMapper.insert(article);
        if (inserted != 1) {
            throw new IllegalStateException("게시글 저장에 실패했습니다.");
        }

        return article.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArticleListView> getArticleList() {
        return articleMapper.selectList();
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleDetailView getArticleDetail(Long articleId) {

        ArticleDetailView articleDetailView = articleMapper.selectDetailById(articleId);
        if (articleDetailView == null) {
            throw new ArticleNotFoundException("게시글을 찾을 수 없습니다.");
        }

        return articleDetailView;
    }

    @Override
    @Transactional(readOnly = true)
    public ArticleEditView getArticleEditView(Long articleId) {

        ArticleEditView articleEditView = articleMapper.selectEditViewById(articleId);
        if (articleEditView == null) {
            throw new ArticleNotFoundException("게시글을 찾을 수 없습니다.");
        }

        return articleEditView;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MyArticleListView> getMyArticleList(Long memberId) {

        return articleMapper.selectMyListByMemberId(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public MyArticleDetailView getMyArticleDetail(Long id) {

        MyArticleDetailView myArticleDetailView = articleMapper.selectMyDetailById(id);
        if (myArticleDetailView == null) {
            throw new ArticleNotFoundException("게시글을 찾을 수 없습니다.");
        }

        return myArticleDetailView;
    }

    @Override
    public void updateArticle(Long articleId, Long loginMemberId, ArticleUpdateForm form) {

        validateOwner(articleId, loginMemberId);

        int updated = articleMapper.update(articleId, form.getTitle(), form.getContent());
        if (updated != 1) {
            throw new IllegalStateException("게시글 수정에 실패했습니다.");
        }
    }

    @Override
    public void deleteArticle(Long articleId, Long loginMemberId) {

        validateOwner(articleId, loginMemberId);

        int deleted = articleMapper.deleteById(articleId);
        if (deleted != 1) {
            throw new IllegalStateException("게시글 삭제에 실패했습니다.");
        }
    }

    @Override
    public void increaseViewCount(Long articleId) {

        int increased = articleMapper.increaseViewCount(articleId);
        if (increased != 1) {
            throw new IllegalStateException("게시글 조회수 증가에 실패했습니다.");
        }
    }

    private void validateOwner(Long articleId, Long loginMemberId) {

        ArticleAuthView articleAuthView = articleMapper.selectAuthById(articleId);

        if (articleAuthView == null) {
            throw new ArticleNotFoundException("게시글을 찾을 수 없습니다.");
        }

        if (!articleAuthView.getMemberId().equals(loginMemberId)) {
            throw new NoPermissionException("본인 게시글만 접근할 수 있습니다.");
        }
    }

}