package com.saebom.bulletinboard.member.service;

import com.saebom.bulletinboard.global.domain.Status;
import com.saebom.bulletinboard.member.dto.MemberSecurityAuthView;
import com.saebom.bulletinboard.member.repository.MemberMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberUserDetailsService implements UserDetailsService {

    private final MemberMapper memberMapper;

    public MemberUserDetailsService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        MemberSecurityAuthView auth = memberMapper.selectSecurityAuthByUsername(username);

        if (auth == null) {
            throw new UsernameNotFoundException("아이디 또는 패스워드가 일치하지 않습니다.");
        }

        if (!Status.ACTIVE.equals(auth.getStatus())) {
            throw new DisabledException("활성화되지 않은 계정입니다.");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(auth.getUsername())
                .password(auth.getPassword())
                .authorities(List.of(new SimpleGrantedAuthority(auth.getRole().name())))
                .build();
    }

}