package com.rejs.flashnote.global.fsrs;
import io.github.openspacedrepetition.CardAndReviewLog;
import io.github.openspacedrepetition.Card;
import io.github.openspacedrepetition.Rating;
import io.github.openspacedrepetition.Scheduler;

public class FsrsUtils {
    public static FsrsMetadata create(){
        Card card = Card.builder().build();
        return FsrsMetadata.from(card);
    }
}
