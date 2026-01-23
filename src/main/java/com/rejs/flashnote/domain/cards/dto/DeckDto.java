package com.rejs.flashnote.domain.cards.dto;

import com.rejs.flashnote.domain.cards.entity.Deck;
import com.rejs.flashnote.domain.cards.entity.DeckOriginalType;
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

    public static DeckDto from(Deck deck){
        return DeckDto.builder()
                .id(deck.getId())
                .name(deck.getName())
                .originalType(deck.getOriginalType())
                .originalId(deck.getOriginalId())
                .cardCounts(deck.getCardCounts())
                .memberId(deck.getMember().getId())
                .build();
    }
}
