package com.saebom.bulletinboard.article.controller;

import com.saebom.bulletinboard.article.dto.*;
import com.saebom.bulletinboard.comment.dto.CommentCreateForm;
import com.saebom.bulletinboard.comment.dto.CommentEditView;
import com.saebom.bulletinboard.comment.dto.CommentUpdateForm;
import com.saebom.bulletinboard.comment.dto.CommentView;
import com.saebom.bulletinboard.article.service.ArticleService;
import com.saebom.bulletinboard.comment.service.CommentService;
import com.saebom.bulletinboard.member.service.MemberService;
import com.saebom.bulletinboard.global.security.CurrentUserId;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final MemberService memberService;

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    public ArticleController(ArticleService articleService,
                             CommentService commentService,
                             MemberService memberService) {
        this.articleService = articleService;
        this.commentService = commentService;
        this.memberService = memberService;
    }

    @GetMapping
    public String list(Model model) {
        List<ArticleListView> articles = articleService.getArticleList();
        model.addAttribute("articles", articles);

        return "articles/list";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            @RequestParam(value = "editCommentId", required = false) Long editCommentId,
            Model model
    ) {

        try {
            articleService.increaseViewCount(id);
        } catch(Exception e) {
            log.warn("게시글 조회수 증가 실패 - articleId={}", id, e);
        }

        ArticleDetailView articleDetailView = articleService.getArticleDetail(id);
        List<CommentView> comments = commentService.getCommentList(id);

        model.addAttribute("article", articleDetailView);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCreateForm", new CommentCreateForm());

        if (editCommentId != null) {

            Long loginMemberId = CurrentUserId.requireMemberId(memberService);

            try {
                commentService.validateCommentBelongsToArticle(editCommentId, id);
            } catch(IllegalArgumentException e) {
                return "redirect:/articles/" + id;
            }

            CommentEditView commentEditView = commentService.getCommentEditView(editCommentId);

            if (!commentEditView.getMemberId().equals(loginMemberId)) {
                return "redirect:/articles/" + id;
            }

            CommentUpdateForm commentUpdateForm = new CommentUpdateForm();
            commentUpdateForm.setContent(commentEditView.getContent());
            model.addAttribute("commentUpdateForm", commentUpdateForm);
            model.addAttribute("editCommentId", editCommentId);
        }

        return "articles/detail";
    }

    @GetMapping("/my")
    public String myList(Model model) {

        Long loginMemberId = CurrentUserId.requireMemberId(memberService);

        List<MyArticleListView> myArticleListView = articleService.getMyArticleList(loginMemberId);
        model.addAttribute("myArticles", myArticleListView);

        return "articles/my-articles";
    }

    @GetMapping("/my/{id}")
    public String myDetail(
            @PathVariable("id") Long id,
            @RequestParam(value = "editCommentId", required = false) Long editCommentId,
            Model model
    ) {

        MyArticleDetailView myArticleDetailView = articleService.getMyArticleDetail(id);
        List<CommentView> comments = commentService.getCommentList(id);

        Long loginMemberId = CurrentUserId.requireMemberId(memberService);

        model.addAttribute("article", myArticleDetailView);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCreateForm", new CommentCreateForm());

        if (editCommentId != null) {

            try {
                commentService.validateCommentBelongsToArticle(editCommentId, id);
            } catch (IllegalArgumentException e) {
                return "redirect:/articles/my/" + id;
            }

            CommentEditView commentEditView = commentService.getCommentEditView(editCommentId);

            if (!commentEditView.getMemberId().equals(loginMemberId)) {
                return "redirect:/articles/my/" + id;
            }

            CommentUpdateForm commentUpdateForm = new CommentUpdateForm();
            commentUpdateForm.setContent(commentEditView.getContent());
            model.addAttribute("commentUpdateForm", commentUpdateForm);
            model.addAttribute("editCommentId", editCommentId);
        }

        return "articles/my-article-detail";
    }

    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) String returnUrl, Model model) {

        model.addAttribute("articleCreateForm", new ArticleCreateForm());
        model.addAttribute("returnUrl", (returnUrl != null ? returnUrl : "/articles"));
        return "articles/new";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("articleCreateForm") ArticleCreateForm form,
            BindingResult bindingResult,
            @RequestParam(required = false) String returnUrl,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("returnUrl", (returnUrl != null ? returnUrl : "/articles"));
            return "articles/new";
        }

        Long loginMemberId = CurrentUserId.requireMemberId(memberService);
        Long articleId = articleService.createArticle(loginMemberId, form);

        redirectAttributes.addFlashAttribute("successMessage", "게시글이 등록되었습니다.");

        String base = (returnUrl != null && !returnUrl.isBlank()) ? returnUrl : "/articles";
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        String target = base + "/" + articleId;

        return "redirect:" + safeReturnUrlOrDefault(target, "/articles/" + articleId);
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               @RequestParam(required = false) String returnUrl,
                               Model model) {

        Long loginMemberId = CurrentUserId.requireMemberId(memberService);

        ArticleEditView articleEditView = articleService.getArticleEditView(id);

        if (!articleEditView.getMemberId().equals(loginMemberId)) {
            return "redirect:/articles/" + id;
        }

        ArticleUpdateForm form = new ArticleUpdateForm();
        form.setTitle(articleEditView.getTitle());
        form.setContent(articleEditView.getContent());

        model.addAttribute("articleUpdateForm", form);
        model.addAttribute("articleId", id);
        model.addAttribute("returnUrl", (returnUrl != null ? returnUrl : "/articles"));

        return "articles/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("articleUpdateForm") ArticleUpdateForm form,
            BindingResult bindingResult,
            @RequestParam(required = false) String returnUrl,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("articleId", id);
            model.addAttribute("returnUrl", (returnUrl != null ? returnUrl : "/articles"));
            return "articles/edit";
        }

        Long loginMemberId = CurrentUserId.requireMemberId(memberService);
        articleService.updateArticle(id, loginMemberId, form);

        redirectAttributes.addFlashAttribute("successMessage", "게시글이 수정되었습니다.");

        String base = (returnUrl != null && !returnUrl.isBlank()) ? returnUrl : "/articles";
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        String target = base + "/" + id;

        return "redirect:" + safeReturnUrlOrDefault(target, "/articles/" + id);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         @RequestParam(value = "returnUrl", required = false) String returnUrl,
                         RedirectAttributes redirectAttributes) {

        Long loginMemberId = CurrentUserId.requireMemberId(memberService);
        articleService.deleteArticle(id, loginMemberId);

        redirectAttributes.addFlashAttribute("successMessage", "게시글이 삭제되었습니다.");

        return "redirect:" + safeReturnUrlOrDefault(returnUrl, "/articles");
    }

    // 헬퍼 메서드
    private String safeReturnUrlOrDefault(String returnUrl, String defaultUrl) {

        if (returnUrl == null || returnUrl.isBlank()) return defaultUrl;
        if (!returnUrl.startsWith("/")) return defaultUrl;
        if (returnUrl.startsWith("//")) return defaultUrl;

        return returnUrl;
    }

}