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
                        // 정적/공용
                        .requestMatchers("/", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()

                        // 로그인/회원가입 관련 (너 프로젝트에 맞춰 유지)
                        .requestMatchers("/login", "/members/new", "/members").permitAll()

                        // "new"는 상세 공개 규칙보다 먼저 잠가야 함 (중요)
                        .requestMatchers("/articles/new").authenticated()

                        // 공개: 목록 (슬래시 버전까지)
                        .requestMatchers(HttpMethod.GET, "/articles", "/articles/").permitAll()

                        // 공개: 상세(숫자 id만)
                        .requestMatchers(articleDetailGetOnly).permitAll()

                        // 관리자
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .requestMatchers("/.well-known/**").permitAll()

                        // 그 외 전부 로그인 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/articles", false)
                        .failureUrl("/login?error")
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