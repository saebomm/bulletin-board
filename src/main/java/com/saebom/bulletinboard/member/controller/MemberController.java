package com.saebom.bulletinboard.member.controller;

import com.saebom.bulletinboard.member.dto.MemberEditView;
import com.saebom.bulletinboard.member.dto.MemberProfileView;
import com.saebom.bulletinboard.member.dto.MemberCreateForm;
import com.saebom.bulletinboard.member.dto.UsernameCheckForm;
import com.saebom.bulletinboard.member.dto.MemberUpdateForm;
import com.saebom.bulletinboard.member.dto.PasswordCheckForm;
import com.saebom.bulletinboard.member.dto.PasswordChangeForm;
import com.saebom.bulletinboard.member.dto.MemberWithdrawForm;
import com.saebom.bulletinboard.member.service.MemberService;
import com.saebom.bulletinboard.global.session.SessionConst;
import com.saebom.bulletinboard.global.exception.WrongPasswordException;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/new")
    public String signUpForm(Model model) {
        model.addAttribute("memberCreateForm", new MemberCreateForm());
        model.addAttribute("usernameChecked", false);
        return "member/signup";
    }

    @PostMapping("/new")
    public String signUp(
            @Valid @ModelAttribute("memberCreateForm") MemberCreateForm form,
            BindingResult bindingResult,
            HttpServletRequest request,
            @RequestParam(defaultValue = "false") boolean usernameChecked,
            Model model
    ) {

        model.addAttribute("usernameChecked", usernameChecked);

        if (!bindingResult.hasFieldErrors("username")) {
            if (memberService.isUsernameDuplicate(form.getUsername())) {
                bindingResult.rejectValue("username", "duplicate", "이미 사용 중인 아이디입니다.");
            }
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "변경하려는 비밀번호가 동일해야합니다.");
            return "member/signup";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("usernameChecked", false);
            return "member/signup";
        }

        Long memberId = memberService.registerMember(form);

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_MEMBER, memberId);

        return "redirect:/articles";
    }

    @GetMapping("/check-username")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkUsername(
            @Valid UsernameCheckForm form,
            BindingResult bindingResult
    ) {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            String message = bindingResult.getAllErrors().get(0).getDefaultMessage();

            response.put("valid", false);
            response.put("duplicate", false);
            response.put("message", message);

            return ResponseEntity.badRequest().body(response);
        }

        String username = form.getUsername();
        boolean duplicate = memberService.isUsernameDuplicate(username);

        response.put("valid", !duplicate);
        response.put("duplicate", duplicate);
        response.put("message",
                duplicate ? "이미 사용 중인 아이디입니다." : "사용 가능한 아이디입니다.");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public String myPage(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long loginMemberId,
            Model model
    ) {

        MemberProfileView memberProfileView = memberService.getMyProfile(loginMemberId);
        model.addAttribute("member", memberProfileView);

        return "member/profile";
    }

    @GetMapping("/me/edit")
    public String editForm(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long loginMemberId,
            Model model
    ) {

        MemberEditView memberEditView = memberService.getMyEditView(loginMemberId);

        MemberUpdateForm form = new MemberUpdateForm();
        form.setName(memberEditView.getName());
        form.setEmail(memberEditView.getEmail());

        model.addAttribute("member", memberEditView);
        model.addAttribute("memberUpdateForm", form);

        return "member/edit";
    }

    @PostMapping("/me/edit")
    public String edit(
            @Valid @ModelAttribute("memberUpdateForm") MemberUpdateForm form,
            BindingResult bindingResult,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long loginMemberId,
            Model model
    ) {

        if (bindingResult.hasErrors()) {
            MemberEditView memberEditView = memberService.getMyEditView(loginMemberId);
            model.addAttribute("member", memberEditView);
            return "member/edit";
        }

        memberService.updateMyProfile(loginMemberId, form);

        return "redirect:/members/me";
    }

    @GetMapping("/me/password/check")
    public String passwordCheckForm(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long loginMemberId,
            Model model
    ) {

        model.addAttribute("passwordCheckForm", new PasswordCheckForm());

        return "member/password-check";
    }

    @PostMapping("/me/password/check")
    public String checkPassword(
            @Valid @ModelAttribute("passwordCheckForm") PasswordCheckForm form,
            BindingResult bindingResult,
            @SessionAttribute(SessionConst.LOGIN_MEMBER) Long loginMemberId,
            HttpServletRequest request
    ) {

        if (bindingResult.hasErrors()) {
            return "member/password-check";
        }

        try {
            memberService.validateMyPassword(loginMemberId, form.getPassword());

            HttpSession session = request.getSession(false);

            if (session == null) {
                throw new IllegalArgumentException("세션이 유효하지 않습니다. 다시 로그인 해주세요.");
            }

            session.setAttribute(SessionConst.PASSWORD_CHECKED, true);

            return "redirect:/members/me/password/new";

        } catch(WrongPasswordException e) {
            bindingResult.rejectValue("password", "wrongPassword", e.getMessage());

            return "member/password-check";
        }

    }

    @GetMapping("/me/password/new")
    public String passwordChangeForm(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Long loginMemberId,
            HttpServletRequest request,
            Model model
    ) {

        HttpSession session = request.getSession(false);
        Boolean passwordChecked = (session != null)
                ? (Boolean) session.getAttribute(SessionConst.PASSWORD_CHECKED)
                : null;

        if (!Boolean.TRUE.equals(passwordChecked)) {
            return "redirect:/members/me/password/check";
        }

        model.addAttribute("passwordChangedAt", memberService.getMyPasswordChangedAt(loginMemberId));
        model.addAttribute("passwordChangeForm", new PasswordChangeForm());

        return "member/password-new";
    }

    @PostMapping("/me/password/new")
    public String changePassword(
            @Valid @ModelAttribute("passwordChangeForm") PasswordChangeForm form,
            BindingResult bindingResult,
            @SessionAttribute(SessionConst.LOGIN_MEMBER) Long loginMemberId,
            HttpServletRequest request,
            Model model
    ) {

        HttpSession session = request.getSession(false);
        Boolean passwordChecked = (session != null)
                ? (Boolean) session.getAttribute(SessionConst.PASSWORD_CHECKED)
                : null;

        if (!Boolean.TRUE.equals(passwordChecked)) {
            return "redirect:/members/me/password/check";
        }

        model.addAttribute("passwordChangedAt", memberService.getMyPasswordChangedAt(loginMemberId));

        if (bindingResult.hasErrors()) {
            return "member/password-new";
        }

        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "변경하려는 비밀번호가 동일해야합니다.");
            return "member/password-new";
        }

        memberService.updateMyPassword(loginMemberId, form.getNewPassword());

        session.removeAttribute(SessionConst.PASSWORD_CHECKED);

        return "redirect:/members/me/password/success";
    }

    @GetMapping("/me/password/success")
    public String passwordSuccessForm(
            @SessionAttribute(SessionConst.LOGIN_MEMBER) Long loginMemberId
    ) {
        return "member/password-success";
    }

    @GetMapping("/me/withdraw")
    public String memberWithdrawForm(
            @SessionAttribute(SessionConst.LOGIN_MEMBER) Long loginMemberId,
            Model model
    ) {

        model.addAttribute("memberWithdrawForm", new MemberWithdrawForm());

        return "member/withdraw";
    }

    @PostMapping("/me/withdraw")
    public String withdrawMember(
            @Valid @ModelAttribute("memberWithdrawForm") MemberWithdrawForm form,
            BindingResult bindingResult,
            @SessionAttribute(SessionConst.LOGIN_MEMBER) Long loginMemberId,
            HttpServletRequest request
    ) {

        if (bindingResult.hasErrors()) {
            return "member/withdraw";
        }

        try {
            memberService.validateMyPassword(loginMemberId, form.getPassword());
        } catch(WrongPasswordException e) {
            bindingResult.rejectValue("password", "wrong", e.getMessage());
            return "member/withdraw";
        }

        memberService.withdrawMyAccount(loginMemberId);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return "redirect:/members/withdraw/success";
    }

    @GetMapping("/withdraw/success")
    public String withdrawSuccessForm() {
        return "member/withdraw-success";
    }

}