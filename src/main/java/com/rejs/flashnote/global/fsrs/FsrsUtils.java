package com.rejs.flashnote.global.fsrs;
import io.github.openspacedrepetition.*;

public class FsrsUtils {
    public static FsrsMetadata create(){
        Card card = Card.builder().build();
        return FsrsMetadata.from(card);
    }

    public static FsrsMetadata study(String currentFsrsJson, Rating rating){
        Card card = Card.fromJson(currentFsrsJson);
        Scheduler scheduler = Scheduler.builder().build();
        CardAndReviewLog cardAndReviewLog = scheduler.reviewCard(card, rating);
        return FsrsMetadata.from(cardAndReviewLog.card());
    }
}
