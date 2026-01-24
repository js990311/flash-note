package com.rejs.flashnote.domain.cards.repository;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.decks.repository.DeckRepository;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.junit.jupiter.api.Assertions.*;

@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
class FlashCardRepositoryTest {

    @Autowired
    private FlashCardRepository flashCardRepository;

    @Autowired
    private CardRepository cardRepository; // 저장을 위해 필요

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeckRepository deckRepository;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();
    private Member member;
    private Deck deck;

    @BeforeEach
    void setUp(){
        member = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        deck = deckRepository.save(fixtureMonkey.giveMeBuilder(Deck.class)
                .set(javaGetter(Deck::getMember), member)
                .sample());
    }

    @Test
    @DisplayName("학습 쿼리 테스트: due가 현재 시각 이전인 카드만 가져오고 due 오름차순으로 정렬해야 한다")
    void findCardsToStudy_Logic_Test() {
        // given
        Instant now = Instant.now();

        // 1. 과거 카드 (복습 대상)
        Card pastCard = createCardWithDue(now.minus(Duration.ofDays(1)));
        // 2. 현재 카드 (복습 대상)
        Card presentCard = createCardWithDue(now);
        // 3. 미래 카드 (대상 아님)
        Card futureCard = createCardWithDue(now.plus(Duration.ofDays(1)));

        cardRepository.saveAll(List.of(pastCard, presentCard, futureCard));

        // when
        List<Card> result = flashCardRepository.findCardsToStudy(member.getId(), deck.getId(), now, 10);

        // then
        assertEquals(2, result.size(), "과거와 현재 카드만 조회되어야 함");
        assertEquals(pastCard.getId(), result.get(0).getId(), "가장 오래된(past) 카드가 첫 번째여야 함");
        assertEquals(presentCard.getId(), result.get(1).getId(), "그다음으로 현재 카드가 와야 함");
    }

    @Test
    @DisplayName("학습 카운트 테스트: 복습 대상 카드의 총 개수를 정확히 반환해야 한다")
    void countCardsToStudy_Test() {
        // given
        Instant now = Instant.now();
        cardRepository.saveAll(List.of(
                createCardWithDue(now.minus(Duration.ofHours(5))),
                createCardWithDue(now.minus(Duration.ofHours(1))),
                createCardWithDue(now.plus(Duration.ofHours(1)))
        ));

        // when
        Long count = flashCardRepository.countCardsToStudy(member.getId(), deck.getId(), now);

        // then
        assertEquals(2, count);
    }

    // 헬퍼 메서드: 특정 due를 가진 카드 생성
    private Card createCardWithDue(Instant due) {
        return fixtureMonkey.giveMeBuilder(Card.class)
                .set(javaGetter(Card::getMember), member)
                .set(javaGetter(Card::getDeck), deck)
                .set(javaGetter(Card::getDue), due)
                .setNull(javaGetter(Card::getDeletedAt))
                .sample();
    }
}