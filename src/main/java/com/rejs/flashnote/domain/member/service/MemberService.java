package com.rejs.flashnote.domain.member.service;

import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    // ## Create
    @Transactional
    public MemberAuthentication getOrCreateAuthentication(String email, String provider){
        Optional<Member> opt = memberRepository.findByEmailAndProvider(email, provider);
        Member member = opt.orElseGet(() -> memberRepository.save(Member.of(email, provider)));
        return MemberAuthentication.from(member);
    }
}
