package com.rejs.flashnote.global.security.utils;

import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.global.security.authentication.OidcMember;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class PrincipalUtils {
    private PrincipalUtils(){}

    public static Optional<MemberAuthentication> getMember(){
        return Optional.ofNullable(SecurityContextHolder.getContextHolderStrategy().getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(OidcMember.class::isInstance)
                .map(principal -> ((OidcMember) principal).getMember());
    }

    public static Long getMemberId() {
        return getMember()
                .map(MemberAuthentication::getId)
                .orElseThrow(IllegalStateException::new);
    }
}
