package com.rejs.flashnote.domain.cards.entity;

import com.rejs.flashnote.domain.cards.dto.request.CreateCardRequest;
import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.global.repository.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @Column
    private String front;

    @Column
    private String back;

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

    public static Card create(Deck deck, Member member, CreateCardRequest request){
        Card card = Card.builder()
                .front(request.getFront())
                .back(request.getBack())
                .build();
        card.mapDeck(deck);
        card.mapMember(member);
        return card;
    }
}
