package com.rejs.flashnote.global.fsrs;

import io.github.openspacedrepetition.Card;
import lombok.Builder;
import lombok.Getter;
import io.github.openspacedrepetition.State;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Getter
public class FsrsMetadata {
    private State state;
    private Instant due;
    private Instant lastReviewAt;
    private String json;

    public static FsrsMetadata from(Card card){
        return FsrsMetadata.builder()
                .due(card.getDue())
                .lastReviewAt(card.getLastReview())
                .state(card.getState())
                .json(card.toJson())
                .build();
    }
}
