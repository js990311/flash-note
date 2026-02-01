package com.rejs.flashnote.domain.cards.service;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.dto.request.CreateCardRequest;
import com.rejs.flashnote.domain.cards.dto.request.UpdateCardRequest;
import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.cards.error.CardException;
import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.cards.repository.CardRepository;
import com.rejs.flashnote.domain.decks.repository.DeckRepository;
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
import java.util.NoSuchElementException;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import({TestcontainersConfiguration.class})
@ActiveProfiles("test")
@SpringBootTest
class CardServiceTest {
    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Autowired
    private CardService cardService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DeckRepository deckRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;
    private Deck savedDeck;

    @BeforeEach
    void setUp() {
        // 1. 회원 저장
        savedMember = memberRepository.save(fixtureMonkey.giveMeOne(Member.class));

        // 2. 덱 저장 (Card 생성에 필수적이므로 미리 세팅)
        Deck deck = fixtureMonkey.giveMeBuilder(Deck.class)
                .set(javaGetter(Deck::getMember), savedMember)
                .sample();
        savedDeck = deckRepository.save(deck);
    }

    @Test
    @DisplayName("카드 생성 통합 테스트: 카드가 저장되고 덱의 cardCounts가 증가해야 한다")
    void createCard_Integration() {
        // given
        CreateCardRequest request = fixtureMonkey.giveMeBuilder(CreateCardRequest.class)
                .set(javaGetter(CreateCardRequest::getDeckId), savedDeck.getId())
                .sample();

        // when
        Long cardId = cardService.createCard(savedMember.getId(), request);

        // then
        // 1. 카드 저장 확인
        assertTrue(cardRepository.existsById(cardId));

        // 2. 덱의 cardCounts 증가 확인 (영속성 컨텍스트 초기화 후 재조회)
        deckRepository.flush();
        Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
        assertEquals(1, updatedDeck.getCardCounts());
    }

    @Test
    @DisplayName("카드 단건 조회: ID로 조회 시 DTO 변환이 올바르게 수행되어야 한다")
    void readById_Integration() {
        // given
        Card card = fixtureMonkey.giveMeBuilder(Card.class)
                .set(javaGetter(Card::getDeck), savedDeck)
                .set(javaGetter(Card::getMember), savedMember)
                .sample();
        Card savedCard = cardRepository.save(card);

        // when
        CardDto result = cardService.readById(savedCard.getId());

        // then
        assertEquals(savedCard.getId(), result.getId());
        assertEquals(savedCard.getFront(), result.getFront());
        assertEquals(savedCard.getBack(), result.getBack());
    }

    @Test
    @DisplayName("카드 단건 조회 실패: 존재하지 않는 ID 조회 시 예외가 발생해야 한다")
    void readById_Fail_NotFound() {
        // when & then
        assertThrows(CardException.class, () -> cardService.readById(9999L));
    }

    @Test
    @DisplayName("덱별 카드 목록 조회: QueryDSL 페이징 쿼리가 정상 작동해야 한다")
    void readPageByDeckId_Integration() {
        // given
        List<Card> cards = fixtureMonkey.giveMeBuilder(Card.class)
                .set(javaGetter(Card::getDeck), savedDeck)
                .set(javaGetter(Card::getMember), savedMember)
                .sampleList(5);
        cardRepository.saveAll(cards);

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<CardDto> result = cardService.readPageByDeckId(savedDeck.getId(), pageable);

        // then
        assertEquals(5, result.getTotalElements());
        assertEquals(5, result.getContent().size());
    }

    @Test
    @DisplayName("카드 수정: Dirty Checking을 통해 앞면과 뒷면이 수정되어야 한다")
    void updateCard_Integration() {
        // given
        Card card = fixtureMonkey.giveMeBuilder(Card.class)
                .set(javaGetter(Card::getDeck), savedDeck)
                .set(javaGetter(Card::getMember), savedMember)
                .sample();
        Card savedCard = cardRepository.saveAndFlush(card);

        String updatedFront = "수정된 앞면";
        String updatedBack = "수정된 뒷면";

        UpdateCardRequest request = fixtureMonkey.giveMeBuilder(UpdateCardRequest.class)
                .set(javaGetter(UpdateCardRequest::getId), savedCard.getId())
                .set(javaGetter(UpdateCardRequest::getFront), updatedFront)
                .set(javaGetter(UpdateCardRequest::getBack), updatedBack)
                .sample();

        // when
        cardService.updateCard(request);
        cardRepository.flush(); // DB 반영 강제

        // then
        Card updatedCard = cardRepository.findById(savedCard.getId()).orElseThrow();
        assertEquals(updatedFront, updatedCard.getFront());
        assertEquals(updatedBack, updatedCard.getBack());
    }

    @Test
    @DisplayName("카드 삭제 통합 테스트: Soft Delete가 수행되고 덱의 cardCounts가 감소해야 한다")
    void deleteCard_Integration() {
        // given
        // 먼저 카드 하나 생성 (연관관계 메서드 작동으로 카운트 1 됨)
        CreateCardRequest request = fixtureMonkey.giveMeBuilder(CreateCardRequest.class)
                .set(javaGetter(CreateCardRequest::getDeckId), savedDeck.getId())
                .sample();
        Long cardId = cardService.createCard(savedMember.getId(), request);

        deckRepository.flush();
        int countAfterCreate = deckRepository.findById(savedDeck.getId()).get().getCardCounts();
        assertEquals(1, countAfterCreate);

        // when
        cardService.deleteCard(cardId);
        deckRepository.flush();

        // then
        // 1. 덱의 cardCounts 감소 확인
        Deck updatedDeck = deckRepository.findById(savedDeck.getId()).orElseThrow();
        assertEquals(0, updatedDeck.getCardCounts());

        // 2. 카드 Soft Delete 확인 (@SQLRestriction("deleted_at IS NULL")에 의해 조회 불가)
        assertTrue(cardRepository.findById(cardId).isEmpty());
    }
}