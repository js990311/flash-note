package com.rejs.flashnote.domain.cards.dto.request;

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
public class CreateCardRequest {
    @NotEmpty
    private String front;
    @NotEmpty
    private String back;
    @NotNull
    private Long deckId;

    public static CreateCardRequest of(Long deckId){
        return CreateCardRequest.builder()
                .deckId(deckId)
                .build();
    }
}
