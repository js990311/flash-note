package com.rejs.flashnote.domain.cards.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class UpdateDeckRequest {
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
}
