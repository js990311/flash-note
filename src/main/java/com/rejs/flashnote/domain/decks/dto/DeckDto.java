package com.rejs.flashnote.domain.decks.dto;

import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.decks.entity.DeckOriginalType;
import com.rejs.flashnote.domain.decks.entity.DeckState;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DeckDto {
    private Long id;
    private String name;
    private DeckOriginalType originalType;
    private Long originalId;
    private Integer cardCounts;
    private Long memberId;
    private DeckState state;

    public static DeckDto from(Deck deck){
        return DeckDto.builder()
                .id(deck.getId())
                .name(deck.getName())
                .originalType(deck.getOriginalType())
                .originalId(deck.getOriginalId())
                .cardCounts(deck.getCardCounts())
                .state(deck.getState())
                .memberId(deck.getMember().getId())
                .build();
    }
}
