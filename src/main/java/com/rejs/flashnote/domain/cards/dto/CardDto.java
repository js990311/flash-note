package com.rejs.flashnote.domain.cards.dto;

import com.rejs.flashnote.domain.cards.entity.Card;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CardDto {
    private Long id;
    private String front;
    private String back;
    private Long deckId;

    public static CardDto from(Card card){
        return CardDto.builder()
                .id(card.getId())
                .front(card.getFront())
                .back(card.getBack())
                .deckId(card.getDeck().getId())
                .build();
    }
}
