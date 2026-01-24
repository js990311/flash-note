package com.rejs.flashnote.domain.cards.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.flashnote.domain.cards.entity.Card;
import com.rejs.flashnote.domain.cards.entity.QCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class FlashCardRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private QCard card = QCard.card;

    @Transactional(readOnly = true)
    public List<Card> findCardsToStudy(Long memberId, Long deckId, Instant now, Integer limit){
        return jpaQueryFactory
                .selectFrom(card)
                .where(
                        card.member.id.eq(memberId),
                        card.deletedAt.isNull(),
                        dueBeforeOrNow(now),
                        deckIdEq(deckId)
                ).orderBy(card.due.asc()).limit(limit != null ? limit : 30).fetch();
    }

    @Transactional(readOnly = true)
    public Long countCardsToStudy(Long memberId, Long deckId, Instant now){
        return jpaQueryFactory
                .select(card.count())
                .from(card)
                .where(
                        card.member.id.eq(memberId),
                        card.deletedAt.isNull(),
                        dueBeforeOrNow(now),
                        deckIdEq(deckId)
                ).fetchOne();
    }


    private BooleanExpression dueBeforeOrNow(Instant now) {
        return now != null ? card.due.loe(now) : null;
    }

    private BooleanExpression deckIdEq(Long deckId) {
        return deckId != null ? card.deck.id.eq(deckId) : null;
    }
}
