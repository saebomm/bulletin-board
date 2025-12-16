package com.saebom.bulletinboard.admin.member.service;

import com.saebom.bulletinboard.global.domain.Status;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberEditView;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberListView;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberUpdateForm;

import java.util.List;

public interface AdminMemberService {

    List<AdminMemberListView> getMemberList(Status status);

    AdminMemberEditView getMemberEditView(Long id);

    void updateStatus(Long adminId, Long memberId, Status status);
    void updateMember(Long adminId, Long memberId, AdminMemberUpdateForm form);
}