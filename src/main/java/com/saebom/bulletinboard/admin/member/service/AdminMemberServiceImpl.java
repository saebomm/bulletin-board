package com.saebom.bulletinboard.admin.member.service;

import com.saebom.bulletinboard.global.domain.Status;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberEditView;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberListView;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberUpdateForm;
import com.saebom.bulletinboard.admin.member.repository.AdminMemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AdminMemberServiceImpl implements AdminMemberService {

    private final AdminMemberMapper adminMemberMapper;

    public AdminMemberServiceImpl(AdminMemberMapper adminMemberMapper) { this.adminMemberMapper = adminMemberMapper; }

    @Override
    public List<AdminMemberListView> getMemberList(Status status) {
        return adminMemberMapper.selectList(status);
    }

    @Override
    public AdminMemberEditView getMemberEditView(Long id) {

        AdminMemberEditView adminMemberEditView = adminMemberMapper.selectEditViewById(id);
        if (adminMemberEditView == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        return adminMemberEditView;
    }

    @Override
    @Transactional
    public void updateStatus(Long adminId, Long memberId, Status status) {

        validateNotSelf(adminId, memberId);
        validateStatusChangeAllowed(status);

        int updated = adminMemberMapper.updateStatus(memberId, status);
        if (updated != 1) {
            throw new IllegalStateException("상태 변경에 실패했습니다.");
        }
    }

    @Override
    @Transactional
    public void updateMember(Long adminId, Long memberId, AdminMemberUpdateForm form) {

        validateNotSelf(adminId, memberId);
        validateStatusChangeAllowed(form.getStatus());

        int updated = adminMemberMapper.update(
                memberId,
                form.getName(),
                form.getEmail(),
                form.getRole(),
                form.getStatus()
        );

        if (updated != 1) {
            throw new IllegalStateException("회원 정보 수정에 실패했습니다.");
        }
    }

    private void validateNotSelf(Long adminId, Long memberId) {

        if (memberId.equals(adminId)) {
            throw new IllegalArgumentException("본인 계정 상태는 변경할 수 없습니다.");
        }
    }

    private void validateStatusChangeAllowed(Status status) {

        if (status == Status.WITHDRAW) {
            throw new IllegalArgumentException("탈퇴 상태는 관리자 변경이 불가합니다.");
        }
    }

}