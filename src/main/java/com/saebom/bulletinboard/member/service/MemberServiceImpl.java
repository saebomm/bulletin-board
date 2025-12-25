package com.saebom.bulletinboard.member.service;

import com.saebom.bulletinboard.member.domain.Member;
import com.saebom.bulletinboard.member.dto.LoginMemberView;
import com.saebom.bulletinboard.member.dto.MemberAuthView;
import com.saebom.bulletinboard.member.dto.MemberEditView;
import com.saebom.bulletinboard.member.dto.MemberProfileView;
import com.saebom.bulletinboard.member.dto.MemberCreateForm;
import com.saebom.bulletinboard.member.dto.MemberUpdateForm;
import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;
import com.saebom.bulletinboard.global.exception.LoginFailedException;
import com.saebom.bulletinboard.global.exception.WrongPasswordException;
import com.saebom.bulletinboard.member.repository.MemberMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberMapper memberMapper, PasswordEncoder passwordEncoder) {
        this.memberMapper = memberMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Long registerMember(MemberCreateForm form) {

        if (isUsernameDuplicate(form.getUsername())) {
            throw new IllegalArgumentException("중복된 회원아이디 입니다.");
        }

        String encodedPassword = passwordEncoder.encode(form.getPassword());

        String normalizedEmail = form.getEmail().trim();
        if (normalizedEmail.isBlank()) {
            normalizedEmail = null;
        }

        Member member = new Member(form.getUsername(), encodedPassword, form.getName(), normalizedEmail);
        member.setRole(Role.USER.value());
        member.setStatus(Status.ACTIVE);

        int inserted = memberMapper.insert(member);
        if (inserted != 1) {
            throw new IllegalStateException("회원 등록에 실패했습니다.");
        }

        return member.getId();
    }

    @Override
    public boolean isUsernameDuplicate(String username) {
        return memberMapper.existsByUsername(username);
    }

    @Override
    public Long loginMember(String username, String password) {

        MemberAuthView memberAuthView = memberMapper.selectAuthByUsername(username);
        if (memberAuthView == null || !passwordEncoder.matches(password, memberAuthView.getPassword())) {
            throw new LoginFailedException("아이디 또는 패스워드가 일치하지 않습니다.");
        }

        if (!Status.ACTIVE.equals(memberAuthView.getStatus())) {
            throw new LoginFailedException("활성화되지 않은 계정입니다.");
        }

        return memberAuthView.getId();
    }

    @Override
    public LoginMemberView getLoginMemberByUsername(String username) {

        return memberMapper.selectLoginMemberByUsername(username);
    }

    @Override
    public Long getMemberIdByUsername(String username) {

        return memberMapper.selectIdByUsername(username);
    }

    @Override
    public MemberProfileView getMyProfile(Long memberId) {

        MemberProfileView memberProfileView = memberMapper.selectProfileById(memberId);
        if (memberProfileView == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        return memberProfileView;
    }

    @Override
    public MemberEditView getMyEditView(Long memberId) {

        MemberEditView memberEditView = memberMapper.selectEditViewById(memberId);
        if (memberEditView == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다.");
        }

        return memberEditView;
    }
    
    @Override
    public LocalDateTime getMyPasswordChangedAt(Long memberId) {
        return memberMapper.selectPasswordChangedAtById(memberId);
    }

    @Override
    public boolean isAdmin(Long memberId) {
        Role role = memberMapper.selectRoleById(memberId);

        return role == Role.ADMIN;
    }

    @Override
    public LoginMemberView getLoginMember(Long memberId) {
        return memberMapper.selectLoginMemberById(memberId);
    }

    @Override
    @Transactional
    public void updateMyProfile(Long memberId, MemberUpdateForm form) {

        String normalizedEmail = form.getEmail().trim();
        if (normalizedEmail.isBlank()) {
            normalizedEmail = null;
        }

        int updated = memberMapper.updateProfile(memberId, form.getName(), normalizedEmail);
        if (updated != 1) {
            throw new IllegalStateException("회원 정보 수정에 실패했습니다.");
        }
    }

    @Override
    public void validateMyPassword(Long id, String rawPassword) {

        MemberAuthView memberAuthView = memberMapper.selectAuthById(id);

        if (memberAuthView == null) {
            throw new IllegalArgumentException("회원을 찾을 수 없습니다. id=" + id);
        }

        if (!passwordEncoder.matches(rawPassword, memberAuthView.getPassword())) {
            throw new WrongPasswordException("패스워드가 일치하지 않습니다.");
        }
    }

    @Override
    @Transactional
    public void updateMyPassword(Long id, String newPassword) {

        String encodedPassword = passwordEncoder.encode(newPassword);

        int updated = memberMapper.updatePassword(id, encodedPassword);
        if (updated != 1) {
            throw new IllegalStateException("패스워드 변경에 실패했습니다. id=" + id);
        }
    }

    @Override
    @Transactional
    public void withdrawMyAccount(Long id) {
        updateStatusInternal(id, Status.WITHDRAW);
    }

    private void updateStatusInternal(Long id, Status status) {

        int updated = memberMapper.updateStatus(id, status);
        if (updated != 1) {
            throw new IllegalStateException("상태 변경에 실패했습니다. id=" + id);
        }
    }

}