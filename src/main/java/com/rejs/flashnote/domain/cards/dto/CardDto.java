package com.rejs.flashnote.domain.cards.dto;

import com.rejs.flashnote.domain.cards.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CardDto {
    private Long id;
    private String front;
    private String back;
    private Long deckId;
    private String state;
    private Instant due;
    private Instant lastReviewAt;

    public static CardDto from(Card card){
        return CardDto.builder()
                .id(card.getId())
                .front(card.getFront())
                .back(card.getBack())
                .deckId(card.getDeck().getId())
                .due(card.getDue())
                .lastReviewAt(card.getLastReviewAt())
                .state(card.getState().toString())
                .build();
    }
}
