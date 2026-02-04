package com.rejs.flashnote.global.security.benchmark;

import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.global.security.authentication.OidcMember;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class BenchmarkOidcMember extends OidcMember {
    private final MemberAuthentication memberAuthentication;

    public BenchmarkOidcMember(MemberAuthentication memberAuthentication) {
        super(null, memberAuthentication);
        this.memberAuthentication = memberAuthentication;
    }

    @Override
    public Map<String, Object> getClaims() {
        return Collections.emptyMap();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return null;
    }

    @Override
    public OidcIdToken getIdToken() {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getName() {
        return memberAuthentication.getEmail();
    }

}
