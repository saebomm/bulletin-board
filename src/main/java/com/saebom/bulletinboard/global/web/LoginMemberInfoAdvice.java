package com.saebom.bulletinboard.global.web;

import com.saebom.bulletinboard.global.security.CurrentUser;
import com.saebom.bulletinboard.member.dto.LoginMemberView;
import com.saebom.bulletinboard.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class LoginMemberInfoAdvice {

    private final MemberService memberService;

    public LoginMemberInfoAdvice(MemberService memberService) {
        this.memberService = memberService;
    }

    @ModelAttribute
    public void addLoginMemberInfo(Model model, HttpServletRequest request) {

        CurrentUser.username().ifPresent(username -> {
            LoginMemberView loginMemberView = memberService.getLoginMemberByUsername(username);
            if (loginMemberView != null) {
                model.addAttribute("loginMember", loginMemberView);
            }
        });

        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();

        model.addAttribute("currentPath", requestURI);

        String currentURL = (queryString != null && !queryString.isBlank())
                ? requestURI + "?" + queryString
                : requestURI;

        model.addAttribute("currentURL", currentURL);
    }
}