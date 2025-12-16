package com.saebom.bulletinboard.global.web;

import com.saebom.bulletinboard.global.session.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginSessionUtils {

    public static Long getLoginMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null)
                ? (Long) session.getAttribute(SessionConst.LOGIN_MEMBER)
                : null;
    }

    public static Long requireLoginMemberId(HttpServletRequest request) {
        Long id = getLoginMemberId(request);
        if (id == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }
        return id;
    }
}