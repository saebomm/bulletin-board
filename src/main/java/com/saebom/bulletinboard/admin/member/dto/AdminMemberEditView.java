package com.saebom.bulletinboard.admin.member.dto;

import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;

import java.time.LocalDateTime;

public class AdminMemberEditView {

    private final Long id;
    private final String username;
    private final String name;
    private final String email;
    private final Role role;
    private final Status status;
    private final LocalDateTime updatedAt;

    // constructor
    public AdminMemberEditView(Long id, String username, String name, String email,
                               Role role, Status status, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    // getter
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public Status getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

}