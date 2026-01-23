package com.rejs.flashnote.domain.cards.entity;

import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.global.repository.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "decks")
@Entity
@Getter
@SQLDelete(sql = "UPDATE decks SET deleted_at = NOW() WHERE deck_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Deck extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deck_id")
    private Long id;

    @Column
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    private DeckOriginalType originalType;

    @Column(nullable = true)
    private Long originalId;

    @Builder.Default
    @Column(nullable = false)
    private Integer cardCounts = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void update(String name) {
        this.name = name;
    }

    public void addCards(){
        cardCounts++;
    }

    public void deleteCards() {
        cardCounts--;
    }

    public static Deck create(String name, Member member) {
        return Deck.builder()
                .name(name)
                .originalType(DeckOriginalType.ORIGINAL)
                .member(member)
                .build();
    }
}
