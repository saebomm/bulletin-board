package com.saebom.bulletinboard.admin.member.dto;

import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;

import java.time.LocalDateTime;

public class AdminMemberListView {

    private final Long id;
    private final String username;
    private final String name;
    private final Role role;
    private final Status status;
    private final LocalDateTime createdAt;

    // constructor
    public AdminMemberListView(Long id, String username, String name, Role role,
                               Status status, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    // getter
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public Role getRole() { return role; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
    public String getRoleText() {
        return isAdmin() ? "관리자" : "일반 회원";
    }

}