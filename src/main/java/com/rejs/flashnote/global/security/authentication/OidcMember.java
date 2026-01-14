package com.rejs.flashnote.global.security.authentication;

import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

public class OidcMember implements OidcUser {
    private OidcUser delegate;
    private MemberAuthentication memberAuthentication;

    public OidcMember(OidcUser delegate, MemberAuthentication memberAuthentication) {
        this.delegate = delegate;
        this.memberAuthentication = memberAuthentication;
    }

    public MemberAuthentication getMember() {
        return memberAuthentication;
    }

    // OIDC 처리

    @Override
    public Map<String, Object> getClaims() {
        return delegate.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return delegate.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return delegate.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return delegate.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return delegate.getAuthorities();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }
}
