package com.rejs.flashnote.common.security;

import com.rejs.flashnote.domain.member.entity.MemberRole;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOidcMemberSecurityContextFactory.class)
public @interface WithMockOidcMember {
    long id() default 1L;
    String name() default "테스트유저이름";
    String email() default "test@example.com";
    MemberRole role() default MemberRole.ROLE_USER;
}
