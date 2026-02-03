package com.rejs.flashnote.domain.member.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.dto.MemberAuthentication;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * 소셜로그인의 핵심이 되는 getOrCretaeAuthentication만 검증
 */
@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
class MemberServiceIntegrationTest {
    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("신규 회원은 Fixture Monkey로 생성된 임의의 데이터로 가입에 성공해야 한다")
    void getOrCreateAuthentication_NewMember() {
        String email = fixtureMonkey.giveMeOne(Member.class).getEmail();
        String provider = "google";

        // when
        MemberAuthentication auth = memberService.getOrCreateAuthentication(email, provider);

        // then
        assertThat(auth).isNotNull();
        assertThat(auth.getEmail()).isEqualTo(email);

        // DB 검증
        Member savedMember = memberRepository.findByEmailAndProvider(email, provider).orElseThrow();
        assertThat(savedMember.getCreatedAt()).isNotNull(); // Auditing 확인
    }

    @Test
    @DisplayName("기존 회원이 존재할 때 Fixture Monkey로 생성된 객체의 ID를 보존해야 한다")
    void getOrCreateAuthentication_ExistingMember() {
        // given
        String email = "test@email.com";
        String provider = "google";
        Member existingMember = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getEmail), email)
                .set(javaGetter(Member::getProvider), provider)
                .sample();
        memberRepository.saveAndFlush(existingMember);
        long countBefore = memberRepository.count();

        // when : 누군가가 똑같은 데이터로 로그인 시도
        MemberAuthentication auth = memberService.getOrCreateAuthentication(email, provider);

        // then
        assertThat(memberRepository.count()).isEqualTo(countBefore);
        assertThat(auth.getEmail()).isEqualTo(email);
    }
}