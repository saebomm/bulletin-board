package com.saebom.bulletinboard.member.repository;

import com.saebom.bulletinboard.member.domain.Member;
import com.saebom.bulletinboard.member.dto.*;
import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface MemberMapper {

    // 회원 저장
    int insert(Member member);

    // 아이디 중복 체크
    boolean existsByUsername(@Param("username") String username);

    // PK로 회원 프로필 조회
    MemberProfileView selectProfileById(@Param("id") Long id);

    // PK로 회원 수정 뷰 조회
    MemberEditView selectEditViewById(@Param("id") Long id);

    // 권한 조회
    MemberAuthView selectAuthById(@Param("id") Long id);
    MemberAuthView selectAuthByUsername(@Param("username") String username);

    // Security 전환: 로그인 권한 조회
    MemberSecurityAuthView selectSecurityAuthByUsername(@Param("username") String username);
    
    // 패스워드 변경일 조회
    LocalDateTime selectPasswordChangedAtById(@Param("id") Long id);

    // 권한 조회
    Role selectRoleById(@Param("id") Long id);

    // 로그인 한 회원 정보 조회
    LoginMemberView selectLoginMemberById(@Param("id") Long id);
    LoginMemberView selectLoginMemberByUsername(@Param("username") String username);

    // username → id 변환
    Long selectIdByUsername(@Param("username") String username);

    // 회원 정보 수정
    int updateProfile(@Param("id") Long id,
                      @Param("name") String name,
                      @Param("email") String email);

    // 회원 패스워드 변경
    int updatePassword(@Param("id") Long id,
                       @Param("password") String password);

    // 회원 상태 변경
    int updateStatus(@Param("id") Long id,
                     @Param("status") Status status);

}