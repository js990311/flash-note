package com.rejs.flashnote.domain.cards.entity;

import com.rejs.flashnote.domain.cards.dto.request.CreateCardRequest;
import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.global.fsrs.FsrsMetadata;
import com.rejs.flashnote.global.fsrs.FsrsUtils;
import com.rejs.flashnote.global.gemini.dto.GeneratedCardDto;
import com.rejs.flashnote.global.repository.entity.BaseEntity;
import io.github.openspacedrepetition.Rating;
import io.github.openspacedrepetition.State;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.Instant;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "cards")
@Entity
@SQLDelete(sql = "UPDATE cards SET deleted_at = NOW() WHERE card_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Getter
public class Card extends BaseEntity {
    @Id
    @Tsid
    @Column(name = "card_id")
    private Long id;

    @Column
    private String front;

    @Column
    private String back;

    @Enumerated(EnumType.STRING)
    @Column
    private State state;
    @Column
    private Instant due;
    @Column
    private Instant lastReviewAt;
    @Column
    private String fsrsJson;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void mapMember(Member member){
        this.member = member;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deck_id")
    private Deck deck;

    public void mapDeck(Deck deck){
        if(this.deck != null){
            this.deck.deleteCards();
        }
        if(deck != null){
            deck.addCards();
        }
        this.deck = deck;
    }

    @Override
    public void delete() {
        if(this.deck != null){
            this.deck.deleteCards();
        }
        super.delete();
    }

    public void update(String front, String back){
        this.front = front;
        this.back = back;
    }

    public void study(Rating rating){
        FsrsMetadata afterStudy = FsrsUtils.study(this.fsrsJson, rating);
        this.due = afterStudy.getDue();
        this.lastReviewAt = afterStudy.getLastReviewAt();
        this.state = afterStudy.getState();
        this.fsrsJson = afterStudy.getJson();
    }

    public static Card create(Deck deck, Member member, CreateCardRequest request){
        FsrsMetadata fsrsMetadata = FsrsUtils.create();
        Card card = Card.builder()
                .front(request.getFront())
                .back(request.getBack())
                .due(fsrsMetadata.getDue())
                .state(fsrsMetadata.getState())
                .lastReviewAt(fsrsMetadata.getLastReviewAt())
                .fsrsJson(fsrsMetadata.getJson())
                .build();
        card.mapDeck(deck);
        card.mapMember(member);
        return card;
    }

    public static Card from(Deck deck, Member member, GeneratedCardDto generatedCard){
        FsrsMetadata fsrsMetadata = FsrsUtils.create();
        Card card = Card.builder()
                .front(generatedCard.getFront())
                .back(generatedCard.getBack())
                .due(fsrsMetadata.getDue())
                .state(fsrsMetadata.getState())
                .lastReviewAt(fsrsMetadata.getLastReviewAt())
                .fsrsJson(fsrsMetadata.getJson())
                .build();
        card.mapDeck(deck);
        card.mapMember(member);
        return card;
    }
}
