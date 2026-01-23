package com.rejs.flashnote.domain.cards.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UpdateDeckRequest {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
}
