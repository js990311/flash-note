package com.rejs.flashnote.domain.cards.controller;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.dto.request.StudyRequest;
import com.rejs.flashnote.domain.cards.service.FlashCardService;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class FlashCardController {
    private final FlashCardService flashCardService;

    @GetMapping("/{deckId}/cards")
    public List<CardDto> getStudyCards(
            @PathVariable("deckId") Long id,
            @RequestParam(value = "limit", defaultValue = "30") Integer limit
    ){
        Long memberId = PrincipalUtils.getMemberId();
        return flashCardService.getTodayFlashCards(memberId, id, limit);
    }

    @PostMapping("/{cardId}")
    public ResponseEntity<Void> updateResource(
            @PathVariable("cardId") Long id,
            @Valid @RequestBody StudyRequest request
    ){
        flashCardService.studyCard(id, request.getRatingValue());
        return ResponseEntity.noContent().build();
    }
}
