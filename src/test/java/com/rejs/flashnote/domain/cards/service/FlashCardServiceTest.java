package com.rejs.flashnote.domain.cards.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.cards.repository.CardRepository;
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
class FlashCardServiceTest {
    @Autowired
    private FlashCardService flashCardService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private MemberRepository memberRepository;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();
    private Member savedMember;
    private Deck savedDeck;

    @BeforeEach
    void setUp() {
        savedMember = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        savedDeck = deckRepository.save(fixtureMonkey.giveMeBuilder(Deck.class)
                .set(javaGetter(Deck::getMember), savedMember)
                .sample());
    }

    @Test
    @DisplayName("오늘의 카드 조회 통합 테스트: 현재 시간 기준 due가 지난 카드만 DTO로 반환되어야 한다")
    void getTodayFlashCards_Integration_Test() {
        // given
        Instant now = Instant.now();

        // 1. 학습 대상인 카드들 (due <= now)
        Card reviewCard1 = createCardWithDue(now.minus(Duration.ofHours(1)));
        Card reviewCard2 = createCardWithDue(now);

        // 2. 학습 대상이 아닌 카드 (due > now)
        Card futureCard = createCardWithDue(now.plus(Duration.ofHours(1)));

        // 3. 다른 사용자의 카드 (조회되면 안 됨)
        Member otherMember = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));
        Card otherUserCard = fixtureMonkey.giveMeBuilder(Card.class)
                .set(javaGetter(Card::getMember), otherMember)
                .set(javaGetter(Card::getDeck), savedDeck)
                .set(javaGetter(Card::getDue), now.minus(Duration.ofHours(1)))
                .sample();

        cardRepository.saveAll(List.of(reviewCard1, reviewCard2, futureCard, otherUserCard));

        // when
        List<CardDto> result = flashCardService.getTodayFlashCards(savedMember.getId(), savedDeck.getId(), 10);

        // then
        // 1. 개수 검증 (본인 카드 중 due가 지난 2개만 나와야 함)
        assertEquals(2, result.size());

        // 2. 정렬 검증 (due 오름차순: 더 오래된 reviewCard1이 먼저)
        assertEquals(reviewCard1.getFront(), result.get(0).getFront());
        assertEquals(reviewCard2.getFront(), result.get(1).getFront());

        // 3. 타입 검증
        assertInstanceOf(CardDto.class, result.get(0));
    }

    @Test
    @DisplayName("오늘의 카드 조회: limit 파라미터가 정상적으로 작동해야 한다")
    void getTodayFlashCards_WithLimit_Test() {
        // given
        Instant now = Instant.now();
        List<Card> manyCards = fixtureMonkey.giveMeBuilder(Card.class)
                .set(javaGetter(Card::getMember), savedMember)
                .set(javaGetter(Card::getDeck), savedDeck)
                .set(javaGetter(Card::getDue), now.minus(Duration.ofMinutes(1)))
                .sampleList(10);
        cardRepository.saveAll(manyCards);

        // when
        int limit = 3;
        List<CardDto> result = flashCardService.getTodayFlashCards(savedMember.getId(), savedDeck.getId(), limit);

        // then
        assertEquals(limit, result.size());
    }

    private Card createCardWithDue(Instant due) {
        return fixtureMonkey.giveMeBuilder(Card.class)
                .set(javaGetter(Card::getMember), savedMember)
                .set(javaGetter(Card::getDeck), savedDeck)
                .set(javaGetter(Card::getDue), due)
                .setNull(javaGetter(Card::getDeletedAt))
                .sample();
    }}