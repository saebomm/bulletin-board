package com.saebom.bulletinboard.member.dto;

import com.saebom.bulletinboard.global.validation.Username;
import com.saebom.bulletinboard.global.validation.Password;
import com.saebom.bulletinboard.global.validation.Name;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class MemberCreateForm {

    @Username
    private String username;

    @Password
    private String password;

    @NotBlank(message = "패스워드를 입력해주세요.")
    private String confirmPassword;

    @Name
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    // getter
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getConfirmPassword() { return confirmPassword; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // setter
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

}