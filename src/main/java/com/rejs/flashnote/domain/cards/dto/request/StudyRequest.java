package com.rejs.flashnote.domain.cards.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudyRequest {
    @NotNull
    @Min(1)
    @Max(4)
    private Integer ratingValue;
}
