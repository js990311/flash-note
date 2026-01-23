package com.rejs.flashnote.domain.decks.controller;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.dto.request.CreateCardRequest;
import com.rejs.flashnote.domain.cards.service.CardService;
import com.rejs.flashnote.domain.decks.dto.DeckDto;
import com.rejs.flashnote.domain.decks.dto.request.CreateDeckRequest;
import com.rejs.flashnote.domain.decks.dto.request.UpdateDeckRequest;
import com.rejs.flashnote.domain.decks.service.DeckService;
import com.rejs.flashnote.global.controller.dto.Pagination;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/decks")
@Controller
public class DeckController {
    private final DeckService deckService;
    private final CardService cardService;

    @GetMapping
    public String getPageDeckDto(@PageableDefault Pageable pageable, Model model){
        Long memberId = PrincipalUtils.getMemberId();
        Page<DeckDto> deckDtos = deckService.readDeckPageByMemberId(memberId, pageable);
        Pagination<DeckDto> decks = Pagination.from(deckDtos);
        model.addAttribute("decks", decks);
        return "decks/page";
    }

    @GetMapping("/{id}")
    public String getDeckById(@PathVariable("id") Long id, @PageableDefault(size = 30) Pageable pageable,Model model){
        DeckDto deckDto = deckService.readDeckById(id);
        Page<CardDto> cardDtos = cardService.readPageByDeckId(deckDto.getId(), pageable);
        model.addAttribute("deck", deckDto);
        model.addAttribute("cards", Pagination.from(cardDtos));
        if(!model.containsAttribute("createCardRequest")){
            model.addAttribute("createCardRequest", CreateCardRequest.of(deckDto.getId()));
        }
        return "decks/id";
    }

    @GetMapping("/create")
    public String getDeckCreate(Model model){
        model.addAttribute("request", new CreateDeckRequest());
        return "decks/create";
    }

    @PostMapping("/create")
    public String postDeckCreate(
            @Valid @ModelAttribute("request") CreateDeckRequest request,
            BindingResult bindingResult,
            Model model
    ){
        if(bindingResult.hasErrors()){
            return "decks/create";
        }
        Long memberId = PrincipalUtils.getMemberId();
        Long deckId = deckService.createDeck(memberId, request);
        return "redirect:/decks/" + deckId;
    }

    @GetMapping("/{id}/update")
    public String getDeckUpdate(
            @PathVariable("id") Long id,
            Model model
    ){
        DeckDto deckDto = deckService.readDeckById(id);
        model.addAttribute("request", UpdateDeckRequest.from(deckDto));
        return "decks/update";
    }

    @PostMapping("/{id}/update")
    public String postDeckUpdate(
            @Valid @ModelAttribute UpdateDeckRequest request,
            BindingResult bindingResult,
            Model model
    ){
        if(bindingResult.hasErrors()){
            return "decks/update";
        }
        Long deckId = deckService.updateDeck(request);
        return "redirect:/decks/" + deckId;
    }

    @PostMapping("/{id}/delete")
    public String postDeckDelete(@PathVariable("id") Long id){
        deckService.deleteDeck(id);
        return "redirect:/decks";
    }
}
