package com.saebom.bulletinboard.article.controller;

import com.saebom.bulletinboard.article.dto.ArticleCreateForm;
import com.saebom.bulletinboard.article.dto.ArticleDetailView;
import com.saebom.bulletinboard.article.dto.ArticleEditView;
import com.saebom.bulletinboard.article.dto.ArticleListView;
import com.saebom.bulletinboard.article.dto.ArticleUpdateForm;
import com.saebom.bulletinboard.comment.dto.CommentCreateForm;
import com.saebom.bulletinboard.comment.dto.CommentEditView;
import com.saebom.bulletinboard.comment.dto.CommentUpdateForm;
import com.saebom.bulletinboard.comment.dto.CommentView;
import com.saebom.bulletinboard.article.service.ArticleService;
import com.saebom.bulletinboard.comment.service.CommentService;
import com.saebom.bulletinboard.global.session.SessionConst;
import com.saebom.bulletinboard.global.web.LoginSessionUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;

    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    public ArticleController(ArticleService articleService, CommentService commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
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
            HttpServletRequest request,
            Model model
    ) {

        try {
            articleService.increaseViewCount(id);
        } catch(Exception e) {
            log.warn("게시글 조회수 증가 실패 - articleId={}", id, e);
        }

        ArticleDetailView articleDetailView = articleService.getArticleDetail(id);
        List<CommentView> comments = commentService.getCommentList(id);

        Long loginMemberId = getLoginMemberId(request);

        model.addAttribute("article", articleDetailView);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCreateForm", new CommentCreateForm());

        if (editCommentId != null) {
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

    @GetMapping("/new")
    public String showCreateForm(HttpServletRequest request, Model model) {

        model.addAttribute("articleCreateForm", new ArticleCreateForm());
        return "articles/new";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("articleCreateForm") ArticleCreateForm form,
            BindingResult bindingResult,
            HttpServletRequest request
    ) {
        if (bindingResult.hasErrors()) {
            return "articles/new";
        }

        Long loginMemberId = LoginSessionUtils.requireLoginMemberId(request);
        Long articleId = articleService.createArticle(loginMemberId, form);

        return "redirect:/articles/" + articleId;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, HttpServletRequest request, Model model) {

        Long loginMemberId = LoginSessionUtils.requireLoginMemberId(request);

        ArticleEditView articleEditView = articleService.getArticleEditView(id);

        if (!articleEditView.getMemberId().equals(loginMemberId)) {
            return "redirect:/articles/" + id;
        }

        ArticleUpdateForm form = new ArticleUpdateForm();
        form.setTitle(articleEditView.getTitle());
        form.setContent(articleEditView.getContent());

        model.addAttribute("articleUpdateForm", form);
        model.addAttribute("articleId", id);

        return "articles/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("articleUpdateForm") ArticleUpdateForm form,
            BindingResult bindingResult,
            HttpServletRequest request,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("articleId", id);
            return "articles/edit";
        }

        Long loginMemberId = LoginSessionUtils.requireLoginMemberId(request);

        articleService.updateArticle(id, loginMemberId, form);

        return "redirect:/articles/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, HttpServletRequest request) {

        Long loginMemberId = LoginSessionUtils.requireLoginMemberId(request);

        articleService.deleteArticle(id, loginMemberId);
        return "redirect:/articles";
    }

    // 헬퍼 메서드
    private Long getLoginMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return (Long) session.getAttribute(SessionConst.LOGIN_MEMBER);
    }

}