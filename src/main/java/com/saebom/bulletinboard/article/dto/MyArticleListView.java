package com.saebom.bulletinboard.article.dto;

import com.saebom.bulletinboard.article.domain.ArticleStatus;

import java.time.LocalDateTime;

public class MyArticleListView {

    private final Long id;
    private final String title;
    private final Integer viewCount;
    private final LocalDateTime createdAt;
    private final ArticleStatus status;
    private final String memberUsername;
    private final String memberName;

    // constructor
    public MyArticleListView(Long id, String title, Integer viewCount, LocalDateTime createdAt,
                             ArticleStatus status, String memberUsername, String memberName) {
        this.id = id;
        this.title = title;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.status = status;
        this.memberUsername = memberUsername;
        this.memberName = memberName;
    }

    // getter
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Integer getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public ArticleStatus getStatus() { return status; }
    public String getMemberUsername() { return memberUsername; }
    public String getMemberName() { return memberName; }

    // helper method
    public boolean isPublic() {
        return status == ArticleStatus.PUBLIC;
    }

    public String getStatusText() {
        return isPublic() ? "공개" : "숨김";
    }

}