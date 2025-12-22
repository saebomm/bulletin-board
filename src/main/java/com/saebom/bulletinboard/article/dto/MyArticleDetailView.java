package com.saebom.bulletinboard.article.dto;

import com.saebom.bulletinboard.article.domain.ArticleStatus;

import java.time.LocalDateTime;

public class MyArticleDetailView {

    private final Long id;
    private final Long memberId;
    private final String title;
    private final String content;
    private final Integer viewCount;
    private final LocalDateTime createdAt;
    private final ArticleStatus status;
    private final String adminMemo;
    private final LocalDateTime adminMemoUpdatedAt;
    private final String memberUsername;
    private final String memberName;

    // constructor
    public MyArticleDetailView(Long id, Long memberId, String title, String content, Integer viewCount,
                               LocalDateTime createdAt, ArticleStatus status, String adminMemo,
                               LocalDateTime adminMemoUpdatedAt, String memberUsername, String memberName) {
        this.id =id;
        this.memberId = memberId;
        this.title = title;
        this.content = content;
        this.viewCount = viewCount;
        this.createdAt = createdAt;
        this.status = status;
        this.adminMemo = adminMemo;
        this.adminMemoUpdatedAt = adminMemoUpdatedAt;
        this.memberUsername = memberUsername;
        this.memberName = memberName;
    }

    // getter
    public Long getId() { return id; }
    public Long getMemberId() { return memberId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Integer getViewCount() { return viewCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public ArticleStatus getStatus() { return status; }
    public String getAdminMemo() { return adminMemo; }
    public LocalDateTime getAdminMemoUpdatedAt() { return adminMemoUpdatedAt; }
    public String getMemberUsername() { return memberUsername; }
    public String getMemberName() { return memberName; }

    // helper method
    public boolean isPublic() { return status == ArticleStatus.PUBLIC; }

    public String getStatusText() { return isPublic() ? "공개" : "숨김"; }

}