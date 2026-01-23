package com.rejs.flashnote.domain.decks.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class CreateDeckRequest {
    @NotEmpty
    private String name;
}
