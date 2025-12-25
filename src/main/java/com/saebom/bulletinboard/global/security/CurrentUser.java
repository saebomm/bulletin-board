package com.saebom.bulletinboard.global.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import java.util.Optional;

public final class CurrentUser {

    private CurrentUser() {}

    public static Optional<String> username() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return Optional.empty();
        if (!auth.isAuthenticated()) return Optional.empty();
        if (auth instanceof AnonymousAuthenticationToken) return Optional.empty();

        String name = auth.getName();
        if (name == null || name.isBlank()) return Optional.empty();

        return Optional.of(name);

    }

    public static String requireUsername() {
        return username().orElseThrow(() -> new IllegalStateException("로그인이 필요합니다."));
    }

}