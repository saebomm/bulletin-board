package com.saebom.bulletinboard.admin.member.repository;

import com.saebom.bulletinboard.admin.member.dto.AdminMemberEditView;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberListView;
import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminMemberMapper {

    // 회원 전체 조회 (상태 조회 포함)
    List<AdminMemberListView> selectList(@Param("status") Status status);

    // PK로 회원 조회
    AdminMemberEditView selectEditViewById(@Param("id") Long id);

    // 회원 상태 변경
    int updateStatus(@Param("id") Long id,
                     @Param("status") Status status);

    // 회원 정보 수정
    int update(@Param("id") Long id,
               @Param("name") String name,
               @Param("email") String email,
               @Param("role") Role role,
               @Param("status") Status status);

}