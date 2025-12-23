package com.saebom.bulletinboard.member.dto;

import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;
import lombok.Getter;

public class MemberSecurityAuthView {

    // getter
    @Getter
    private final Long id;

    @Getter
    private final String username;

    @Getter
    private final String password;

    @Getter
    private final Role role;

    @Getter
    private final Status status;

    // constructor
    public MemberSecurityAuthView(Long id, String username, String password, Role role, Status status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

}