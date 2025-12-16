package com.saebom.bulletinboard.member.dto;

import java.time.LocalDateTime;

public class MemberProfileView {

    private final String username;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    // constructor
    public MemberProfileView(String username, String name, String email,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getter
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

}