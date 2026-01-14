package com.rejs.flashnote.domain.member.dto;

import com.rejs.flashnote.domain.member.entity.Member;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

@Getter
public class MemberAuthentication {
    private Long id;
    private String name;
    private String email;
    private List<? extends GrantedAuthority> authorities;

    public MemberAuthentication(Long id, String name, String email, List<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.authorities = authorities;
    }

    public static MemberAuthentication from(Member member){
        return new MemberAuthentication(member.getId(), member.getName(), member.getEmail(), Collections.singletonList(new SimpleGrantedAuthority(member.getRole().name())));
    }
}
