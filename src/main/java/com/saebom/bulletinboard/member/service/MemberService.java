package com.saebom.bulletinboard.member.service;

import com.saebom.bulletinboard.member.dto.LoginMemberView;
import com.saebom.bulletinboard.member.dto.MemberEditView;
import com.saebom.bulletinboard.member.dto.MemberProfileView;
import com.saebom.bulletinboard.member.dto.MemberCreateForm;
import com.saebom.bulletinboard.member.dto.MemberUpdateForm;

import java.time.LocalDateTime;

public interface MemberService {

    // 회원 가입
    Long registerMember(MemberCreateForm form);

    // 아이디 중복 체크
    boolean isUsernameDuplicate(String username);

    // 로그인
    Long loginMember(String username, String password);

    // 프로필
    MemberProfileView getMyProfile(Long memberId);
    MemberEditView getMyEditView(Long memberId);

    // 패스워드 변경일
    LocalDateTime getMyPasswordChangedAt(Long memberId);

    // 권한 조회
    boolean isAdmin(Long memberId);

    // 로그인 회원 조회
    LoginMemberView getLoginMember(Long memberId);
    LoginMemberView getLoginMemberByUsername(String username);

    // username → id 변환 메서드
    Long getMemberIdByUsername(String username);

    // 프로필 업데이트
    void updateMyProfile(Long memberId, MemberUpdateForm form);

    // 패스워드
    void validateMyPassword(Long id, String rawPassword);
    void updateMyPassword(Long id, String newPassword);

    // 탈퇴
    void withdrawMyAccount(Long id);

}