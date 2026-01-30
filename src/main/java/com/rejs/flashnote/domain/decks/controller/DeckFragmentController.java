package com.rejs.flashnote.domain.decks.controller;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.service.CardService;
import com.rejs.flashnote.domain.decks.dto.DeckDto;
import com.rejs.flashnote.domain.decks.service.DeckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/decks")
@Controller
public class DeckFragmentController {
    private final DeckService deckService;
    private final CardService cardService;

    @ResponseBody
    @GetMapping("/{id}/state")
    public Map<String, String> getDeckState(@PathVariable("id") Long id){
        DeckDto deckDto = deckService.readDeckById(id);
        return Map.of("state", deckDto.getState().toString());
    }

    @GetMapping("/{id}/fail-fragment")
    public String getDeckFragmentFail(@PathVariable("id") Long id){
        return "decks/fragments/ai_gen_failed";
    }

    @GetMapping("/{id}/cards")
    public String getDeckFragmentCards(@PathVariable("id") Long id, @PageableDefault Pageable pageableDefault, Model model){
        Page<CardDto> cardDtos = cardService.readPageByDeckId(id, pageableDefault);
        model.addAttribute("cards", cardDtos);
        return "decks/fragments/cards_fragments";
    }
}
