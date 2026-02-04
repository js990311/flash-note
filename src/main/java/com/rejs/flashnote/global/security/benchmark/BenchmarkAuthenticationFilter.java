package com.rejs.flashnote.global.security.benchmark;

import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import com.rejs.flashnote.domain.member.service.MemberService;
import com.rejs.flashnote.global.security.authentication.OidcMember;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * k6 테스트 등 인증 우회가 필요한 경우
 */
@Component
@Profile("benchmark")
@RequiredArgsConstructor
public class BenchmarkAuthenticationFilter extends OncePerRequestFilter {
    private final MemberService memberService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String testMemberEmail = request.getHeader("X-Test-Member-Email");
        if (testMemberEmail != null) {
            MemberAuthentication orCreateAuthentication = memberService.getOrCreateAuthentication(testMemberEmail, "test");
            OidcMember oidcMember = new OidcMember(null, orCreateAuthentication);
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(oidcMember, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))));
        }
        filterChain.doFilter(request,response);
    }
}
