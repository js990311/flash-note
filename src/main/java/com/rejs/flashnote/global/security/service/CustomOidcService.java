package com.rejs.flashnote.global.security.service;

import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.domain.member.service.MemberService;
import com.rejs.flashnote.global.security.authentication.OidcMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOidcService extends OidcUserService {
    private final MemberService memberService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();
        String provider = userRequest.getClientRegistration().getRegistrationId();
        MemberAuthentication orCreateAuthentication = memberService.getOrCreateAuthentication(email, provider);
        return new OidcMember(oidcUser,orCreateAuthentication);
    }
}
