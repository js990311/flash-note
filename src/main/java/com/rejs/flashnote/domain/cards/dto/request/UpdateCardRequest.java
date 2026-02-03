package com.rejs.flashnote.domain.cards.dto.request;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateCardRequest {
    @NotNull
    private Long id;
    @NotEmpty
    private String front;
    @NotEmpty
    private String back;

    public static UpdateCardRequest from(CardDto cardDto) {
        return UpdateCardRequest.builder()
                .id(cardDto.getId())
                .front(cardDto.getFront())
                .back(cardDto.getBack())
                .build();
    }
}
