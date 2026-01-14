package com.rejs.flashnote.domain.member.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.navercorp.fixturemonkey.api.introspector.BuilderArbitraryIntrospector;
import com.rejs.flashnote.TestcontainersConfiguration;
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
    private final FixtureMonkey fixtureMonkey = FixtureMonkey.builder()
            .objectIntrospector(BuilderArbitraryIntrospector.INSTANCE)
            .build();

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("신규 회원은 Fixture Monkey로 생성된 임의의 데이터로 가입에 성공해야 한다")
    void getOrCreateAuthentication_NewMember() {
        // given: 임의의 이메일과 공급자 생성
        String email = fixtureMonkey.giveMeOne(String.class); // 혹은 특정 패턴의 문자열
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
        // given: Fixture Monkey로 기존 멤버 객체 생성
        String email = "test@flashnote.com";
        String provider = "google";

        // Member.of() 대신 Fixture Monkey를 사용하여 엔티티를 직접 생성(Mocking 시나리오와 유사)
        Member existingMember = fixtureMonkey.giveMeBuilder(Member.class)
                .set(javaGetter(Member::getEmail), email)
                .set(javaGetter(Member::getProvider), provider)
                .setNull(javaGetter(Member::getId)) // ID는 DB가 할당하도록 null 설정
                .sample();

        memberRepository.save(existingMember);
        long countBefore = memberRepository.count();

        // when
        MemberAuthentication auth = memberService.getOrCreateAuthentication(email, provider);

        // then
        assertThat(memberRepository.count()).isEqualTo(countBefore);
        assertThat(auth.getEmail()).isEqualTo(email);
    }
}