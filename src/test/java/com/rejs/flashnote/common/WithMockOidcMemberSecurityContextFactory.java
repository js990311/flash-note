package com.rejs.flashnote.common;

import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.global.security.authentication.OidcMember;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Annotation;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class WithMockOidcMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockOidcMember> {
    @Override
    public SecurityContext createSecurityContext(WithMockOidcMember annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // 1. MemberAuthentication 생성 (신경 써야 할 부분)
        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(annotation.role().name()));
        MemberAuthentication
                memberAuth = new MemberAuthentication(
                annotation.id(),
                annotation.name(),
                annotation.email(),
                authorities
        );

        // 2. Delegate (OidcUser) 생성
        // OidcMember 내부에서 delegate의 메소드를 호출하므로 최소한의 데이터는 넣어줘야 합니다.
        OidcIdToken idToken = new OidcIdToken(
                "mock-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("sub", annotation.email(), "email", annotation.email())
        );
        OidcUser delegate = new DefaultOidcUser(authorities, idToken);

        // 3. 최종 OidcMember 생성
        OidcMember principal = new OidcMember(delegate, memberAuth);

        // 4. Authentication 객체 생성 및 Context 설정
        Authentication auth = new OAuth2AuthenticationToken(
                principal,
                authorities,
                "google" // 테스트용 클라이언트 ID
        );

        context.setAuthentication(auth);
        return context;
    }
}
