package com.rejs.flashnote.domain.decks.dto.generate;

import com.rejs.flashnote.domain.decks.entity.Deck;
import com.rejs.flashnote.domain.note.entity.Note;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GenerateDeckFromNoteDto {
    private Long deckId;
    private String notes;

    public static GenerateDeckFromNoteDto from(Deck deck, Note note){
        String notes = String.format("title:{}\n\ncontent:{}", note.getTitle(), note.getContent());
        return GenerateDeckFromNoteDto.builder()
                .deckId(deck.getId())
                .notes(notes)
                .build();
    }
}
