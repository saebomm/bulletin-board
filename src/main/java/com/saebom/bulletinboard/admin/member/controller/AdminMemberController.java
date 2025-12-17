package com.saebom.bulletinboard.admin.member.controller;

import com.saebom.bulletinboard.global.domain.Role;
import com.saebom.bulletinboard.global.domain.Status;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberEditView;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberListView;
import com.saebom.bulletinboard.admin.member.dto.AdminMemberUpdateForm;
import com.saebom.bulletinboard.global.web.LoginSessionUtils;
import com.saebom.bulletinboard.admin.member.service.AdminMemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class AdminMemberController {

    private final AdminMemberService adminMemberService;

    public AdminMemberController(AdminMemberService adminMemberService) { this.adminMemberService = adminMemberService; }

    @GetMapping
    public String list(@RequestParam(required = false) Status status, Model model) {

        List<AdminMemberListView> members = adminMemberService.getMemberList(status);

        model.addAttribute("members", members);
        model.addAttribute("statuses", Status.values());

        return "admin/members/list";
    }

    @GetMapping("/{memberId}")
    public String editForm(@PathVariable Long memberId, Model model) {

        AdminMemberEditView adminMemberEditView = adminMemberService.getMemberEditView(memberId);

        AdminMemberUpdateForm form = new AdminMemberUpdateForm();
        form.setName(adminMemberEditView.getName());
        form.setEmail(adminMemberEditView.getEmail());
        form.setRole(adminMemberEditView.getRole());
        form.setStatus(adminMemberEditView.getStatus());

        model.addAttribute("member", adminMemberEditView);
        model.addAttribute("adminMemberUpdateForm", form);
        model.addAttribute("statuses", Status.values());
        model.addAttribute("roles", Role.values());

        return "admin/members/edit";
    }

    @PostMapping("/{memberId}/status")
    public String changeStatus(
            @PathVariable Long memberId,
            @RequestParam("status") Status status,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        Long adminId = LoginSessionUtils.requireLoginMemberId(request);

        try {
            adminMemberService.updateStatus(adminId, memberId, status);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "회원 상태가 정상적으로 변경되었습니다."
            );
        } catch (IllegalArgumentException e) {
            // 정책 위반(본인 변경, WITHDRAW 요청 등)
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage()
            );
        } catch (IllegalStateException e) {
            // rowcount 불일치 등 처리 실패
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "상태 변경에 실패했습니다."
            );
        }

        return "redirect:/admin/members";
    }

    @PostMapping("/{memberId}")
    public String edit(
            @Valid @ModelAttribute("adminMemberUpdateForm") AdminMemberUpdateForm form,
            BindingResult bindingResult,
            @PathVariable("memberId") Long memberId,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes,
            Model model
    ) {

        Long adminId = LoginSessionUtils.requireLoginMemberId(request);

        if (bindingResult.hasErrors()) {
            AdminMemberEditView view = adminMemberService.getMemberEditView(memberId);
            model.addAttribute("member", view);
            model.addAttribute("statuses", Status.values());
            model.addAttribute("roles", Role.values());
            return "admin/members/edit";
        }

        try {
            adminMemberService.updateMember(adminId, memberId, form);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "회원 정보가 성공적으로 변경되었습니다."
            );
        } catch (IllegalArgumentException e) {
            // 정책 위반 → edit 화면에 메시지 + 그대로 렌더링
            AdminMemberEditView view = adminMemberService.getMemberEditView(memberId);
            model.addAttribute("member", view);
            model.addAttribute("statuses", Status.values());
            model.addAttribute("roles", Role.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/members/edit";
        } catch (IllegalStateException e) {
            // rowcount 불일치 등
            AdminMemberEditView view = adminMemberService.getMemberEditView(memberId);
            model.addAttribute("member", view);
            model.addAttribute("statuses", Status.values());
            model.addAttribute("roles", Role.values());
            model.addAttribute("errorMessage", "회원 정보 수정에 실패했습니다.");
            return "admin/members/edit";
        }

        return "redirect:/admin/members";
    }

}