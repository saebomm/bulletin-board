package com.saebom.bulletinboard.global.config;

import com.saebom.bulletinboard.member.service.MemberService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MemberService memberService;

    public WebConfig(MemberService memberService) { this.memberService = memberService; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new AdminCheckInterceptor(memberService))
                .order(0)
                .addPathPatterns(
                        "/admin/**"
                )
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/error"
                );

        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns(
                        "/articles/new",
                        "/articles/*/edit",
                        "/articles/*/delete",
                        "/comments/**",
                        "/members/me/**"
                )
                .excludePathPatterns(
                        "/",
                        "/login",
                        "/logout",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/error"
                );

    }

}