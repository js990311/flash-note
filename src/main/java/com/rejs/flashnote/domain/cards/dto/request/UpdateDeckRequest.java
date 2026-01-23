package com.rejs.flashnote.domain.cards.dto.request;

import com.rejs.flashnote.domain.cards.dto.DeckDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class UpdateDeckRequest {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;

    public static UpdateDeckRequest from(DeckDto deckDto) {
        return UpdateDeckRequest.builder()
                .id(deckDto.getId())
                .name(deckDto.getName())
                .build();
    }
}
