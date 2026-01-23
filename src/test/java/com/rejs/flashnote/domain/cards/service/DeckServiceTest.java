package com.rejs.flashnote.domain.cards.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.dto.DeckDto;
import com.rejs.flashnote.domain.cards.dto.request.CreateDeckRequest;
import com.rejs.flashnote.domain.cards.dto.request.UpdateDeckRequest;
import com.rejs.flashnote.domain.cards.entity.Deck;
import com.rejs.flashnote.domain.cards.repository.DeckRepository;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
class DeckServiceTest {
    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Autowired
    private DeckService deckService;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    @BeforeEach
    void setup(){
        Member member = fixtureMonkey.giveMeOne(Member.class);
        savedMember = memberRepository.save(member);
    }

    @Test
    @DisplayName("덱 생성 통합 테스트: DB에 영속화되고 생성된 ID를 반환해야 한다")
    void createDeck_Integration() {
        // given
        CreateDeckRequest request = fixtureMonkey.giveMeOne(CreateDeckRequest.class);

        // when
        Long deckId = deckService.createDeck(savedMember.getId(), request);

        // then
        assertThat(deckId).isNotNull();

        // 실제 DB 조회 검증
        Deck foundDeck = deckRepository.findById(deckId).orElseThrow();
        assertThat(foundDeck.getName()).isEqualTo(request.getName());
        assertThat(foundDeck.getMember().getId()).isEqualTo(savedMember.getId());
    }

    @Test
    @DisplayName("덱 조회 통합 테스트: 저장된 데이터가 DTO로 올바르게 변환되어야 한다")
    void readDeckById_Integration() {
        // given
        Deck deck = fixtureMonkey.giveMeBuilder(Deck.class)
                .set(javaGetter(Deck::getMember), savedMember)
                .sample();
        Deck targetDeck = deckRepository.save(deck);

        // when
        DeckDto result = deckService.readDeckById(targetDeck.getId());

        // then
        assertThat(result.getId()).isEqualTo(targetDeck.getId());
        assertThat(result.getName()).isEqualTo(targetDeck.getName());
    }

    @Test
    @DisplayName("페이징 조회 통합 테스트: 실제 DB 쿼리로 멤버의 덱 목록을 가져와야 한다")
    void readDeckPageByMemberId_Integration() {
        // given
        int count = 5;
        List<Deck> decks = fixtureMonkey.giveMeBuilder(Deck.class)
                .set(javaGetter(Deck::getMember), savedMember)
                .sampleList(count);
        deckRepository.saveAll(decks);

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<DeckDto> result = deckService.readDeckPageByMemberId(savedMember.getId(), pageable);

        // then
        assertThat(result.getTotalElements()).isGreaterThanOrEqualTo(count);
        assertThat(result.getContent()).allSatisfy(dto ->
                assertThat(dto.getMemberId()).isEqualTo(savedMember.getId())
        );
    }

    @Test
    @DisplayName("덱 수정 통합 테스트: Dirty Checking이 실제 트랜잭션 내에서 반영되어야 한다")
    void updateDeck_Integration() {
        // given
        Deck deck = fixtureMonkey.giveMeBuilder(Deck.class)
                .set(javaGetter(Deck::getMember), savedMember)
                .sample();
        Deck savedDeck = deckRepository.saveAndFlush(deck);

        String updatedName = "수정된 덱 이름";
        UpdateDeckRequest request = fixtureMonkey.giveMeBuilder(UpdateDeckRequest.class)
                .set(javaGetter(UpdateDeckRequest::getId), savedDeck.getId())
                .set(javaGetter(UpdateDeckRequest::getName), updatedName)
                .sample();

        // when
        deckService.updateDeck(request);

        // then
        // 영속성 컨텍스트가 갱신된 실제 DB 값을 다시 확인
        Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
        assertThat(updatedDeck.getName()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("덱 삭제 통합 테스트: DB에서 해당 데이터가 완전히 제거되어야 한다")
    void deleteDeck_Integration() {
        // given
        Deck deck = fixtureMonkey.giveMeBuilder(Deck.class)
                .set(javaGetter(Deck::getMember), savedMember)
                .sample();
        Deck savedDeck = deckRepository.save(deck);

        // when
        deckService.deleteDeck(savedDeck.getId());

        // then
        assertThat(deckRepository.findById(savedDeck.getId())).isEmpty();
    }
}