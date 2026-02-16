package com.rejs.flashnote.domain.member.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.domain.member.dto.ProfileDto;
import com.rejs.flashnote.domain.member.dto.request.UpdateProfileRequest;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.error.MemberException;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;
    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("인증 조회/생성: 이미 존재하면 저장하지 않고 기존 멤버로 MemberAuthentication을 반환한다")
    void getOrCreateAuthentication_exists() {
        // given
        String email = "test@test.com";
        String provider = "google";
        Long memberId = 10L;

        Member existing = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getId), memberId)
                .set(javaGetter(Member::getEmail), email)
                .set(javaGetter(Member::getProvider), provider)
                .sample();

        given(memberRepository.findByEmailAndProvider(email, provider))
                .willReturn(Optional.of(existing));

        // when
        MemberAuthentication result = memberService.getOrCreateAuthentication(email, provider);

        // then
        assertNotNull(result);
        assertEquals(memberId, result.getId());

        then(memberRepository).should().findByEmailAndProvider(email, provider);
        then(memberRepository).should(never()).save(any(Member.class));
    }

    @Test
    @DisplayName("인증 조회/생성: 존재하지 않으면 새 멤버를 저장하고 MemberAuthentication을 반환한다")
    void getOrCreateAuthentication_create() {
        // given
        String email = "new@test.com";
        String provider = "google";
        Long generatedId = 99L;

        Member saved = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getId), generatedId)
                .set(javaGetter(Member::getEmail), email)
                .set(javaGetter(Member::getProvider), provider)
                .sample();

        given(memberRepository.findByEmailAndProvider(email, provider))
                .willReturn(Optional.empty());
        given(memberRepository.save(any(Member.class)))
                .willReturn(saved);

        // when
        MemberAuthentication result = memberService.getOrCreateAuthentication(email, provider);

        // then
        assertNotNull(result);
        assertEquals(generatedId, result.getId());

        then(memberRepository).should().findByEmailAndProvider(email, provider);
        then(memberRepository).should().save(any(Member.class));
    }

    @Test
    @DisplayName("프로필 조회: 존재하는 멤버 ID면 ProfileDto를 반환한다")
    void readProfile_success() {
        // given
        Long memberId = 1L;

        Member member = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getId), memberId)
                .sample();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        ProfileDto result = memberService.readProfile(memberId);

        // then
        assertNotNull(result);
        assertEquals(memberId, result.getId());

        then(memberRepository).should().findById(memberId);
    }

    @Test
    @DisplayName("프로필 조회: 존재하지 않는 멤버 ID면 MemberException.notFound 예외가 발생한다")
    void readProfile_notFound() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MemberException.class, () -> memberService.readProfile(memberId));
        then(memberRepository).should().findById(memberId);
    }

    @Test
    @DisplayName("프로필 수정: 이름을 수정하면 변경 사항이 반영되고 ProfileDto를 반환한다")
    void updateProfile_success() {
        // given
        Long memberId = 10L;

        // null 가능성이 있으면 아래처럼 고정값 세팅 권장
        UpdateProfileRequest request = fixtureMonkey.giveMeBuilder(UpdateProfileRequest.class)
                .set("name", "new-name")
                .sample();
        String newName = request.getName();

        Member member = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getId), memberId)
                .sample();

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        ProfileDto result = memberService.updateProfile(memberId, request);

        // then
        assertNotNull(result);
        assertEquals(memberId, result.getId());
        assertEquals(newName, result.getName());

        // Dirty Checking을 위한 상태 변경 검증
        assertEquals(newName, member.getName());

        then(memberRepository).should().findById(memberId);
    }

    @Test
    @DisplayName("프로필 수정: 존재하지 않는 멤버 ID면 MemberException.notFound 예외가 발생한다")
    void updateProfile_notFound() {
        // given
        Long memberId = 999L;
        UpdateProfileRequest request = fixtureMonkey.giveMeOne(UpdateProfileRequest.class);

        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MemberException.class, () -> memberService.updateProfile(memberId, request));

        then(memberRepository).should().findById(memberId);
        then(memberRepository).should(never()).save(any(Member.class));
    }


}