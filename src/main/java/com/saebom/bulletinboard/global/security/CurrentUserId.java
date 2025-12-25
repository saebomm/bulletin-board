package com.saebom.bulletinboard.global.security;

import com.saebom.bulletinboard.member.service.MemberService;

public final class CurrentUserId {

    private CurrentUserId() {}

    public static Long requireMemberId(MemberService memberService) {

        String username = CurrentUser.requireUsername();
        Long memberId = memberService.getMemberIdByUsername(username);

        if (memberId == null) {
            throw new IllegalStateException("로그인 사용자 정보를 찾을 수 없습니다.");
        }

        return memberId;
    }
}