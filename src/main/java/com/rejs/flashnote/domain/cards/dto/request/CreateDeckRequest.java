package com.rejs.flashnote.domain.cards.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CreateDeckRequest {
    @NotEmpty
    private String name;
}
