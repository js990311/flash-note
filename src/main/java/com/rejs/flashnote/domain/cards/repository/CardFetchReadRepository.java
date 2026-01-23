package com.rejs.flashnote.domain.cards.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.cards.entity.QCard;
import com.rejs.flashnote.domain.decks.entity.QDeck;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardFetchReadRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private QCard card = QCard.card;
    private QDeck deck = QDeck.deck;

    public Card findWithDeckById(Long cardId){
        return jpaQueryFactory
                .selectFrom(card)
                .join(card.deck, deck).fetchJoin()
                .where(card.id.eq(cardId))
                .fetchOne();
    }

    public Page<Card> findCardsByDeckId(Long deckId, Pageable pageable) {
        List<Card> cards = jpaQueryFactory
                .select(card)
                .from(card)
                .where(card.deck.id.eq(deckId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countq = jpaQueryFactory
                .select(card.count())
                .from(card)
                .where(card.deck.id.eq(deckId));
        return PageableExecutionUtils.getPage(cards, pageable, countq::fetchOne);
    }
}
