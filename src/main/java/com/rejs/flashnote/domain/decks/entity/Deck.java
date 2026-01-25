package com.rejs.flashnote.domain.decks.entity;

import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.domain.note.entity.Note;
import com.rejs.flashnote.global.repository.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
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
    @Tsid
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

    @Enumerated(EnumType.STRING)
    @Column
    private DeckState state;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void complete(){
        this.state = DeckState.COMPLETED;
    }

    public void fail(){
        this.state = DeckState.AI_GEN_FAILED;
    }

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
                .state(DeckState.COMPLETED)
                .member(member)
                .build();
    }

    public static Deck from(Note note, Member member) {
        return Deck.builder()
                .name(note.getTitle() + "의 카드들")
                .originalType(DeckOriginalType.DECK)
                .state(DeckState.AI_GENERATING)
                .member(member)
                .build()
        ;
    }

}
