package com.saebom.bulletinboard.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        RequestMatcher articleDetailGetOnly =
                new RegexRequestMatcher("^/articles/\\d+$", HttpMethod.GET.name());

        http
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()

                        // 공개: 게시글 목록
                        .requestMatchers(HttpMethod.GET, "/articles").permitAll()

                        // 공개: 게시글 상세 (숫자 ID만)
                        .requestMatchers(articleDetailGetOnly).permitAll()

                        // 로그인/회원가입 화면 (필요시 조정)
                        .requestMatchers("/login", "/members/new", "/members").permitAll()

                        // 관리자
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 그 외 전부 로그인 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/articles")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}