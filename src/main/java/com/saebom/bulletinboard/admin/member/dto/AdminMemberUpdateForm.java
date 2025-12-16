package com.saebom.bulletinboard.admin.member.dto;

import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;
import com.saebom.bulletinboard.global.validation.Name;
import jakarta.validation.constraints.Email;

public class AdminMemberUpdateForm {

    @Name
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    private Role role;

    private Status status;

    // getter
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
    public Status getStatus() { return status; }

    // setter
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(Role role) { this.role = role; }
    public void setStatus(Status status) { this.status = status; }

}