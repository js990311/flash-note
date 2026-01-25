package com.rejs.flashnote.domain.decks.service.generate;

import com.rejs.flashnote.domain.decks.dto.generate.GenerateDeckFromNoteDto;
import com.rejs.flashnote.domain.decks.service.DeckService;
import com.rejs.flashnote.domain.note.service.NoteService;
import com.rejs.flashnote.global.gemini.dto.GeneratedDeckDto;
import com.rejs.flashnote.global.gemini.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenerateDeckFacade {
    private final GeminiService geminiService;
    private final GenerateDeckService generateDeckService;

    public Long generateFlashCardFromNoteId(Long noteId, Long memberId){
        GenerateDeckFromNoteDto generateDeckFromNoteDto = generateDeckService.generateDeckFromNote(noteId, memberId);
        final Long deckId = generateDeckFromNoteDto.getDeckId();
        CompletableFuture
                .supplyAsync(
                    ()->geminiService.readCards(generateDeckFromNoteDto.getNotes())
                )
                .thenAcceptAsync(generatedDeckDto -> {
                    generateDeckService.generateCard(deckId, memberId, generatedDeckDto);
                    log.info("Deck {} 생성 성공", deckId);
                })
                .exceptionally(ex->{
                    log.error("Deck {} 생성 중 오류 {}", deckId, ex);
                    generateDeckService.generateFail(deckId);
                    return null;
                });
        ;
        return deckId;
    }
}
