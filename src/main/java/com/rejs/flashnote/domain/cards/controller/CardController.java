package com.rejs.flashnote.domain.cards.controller;

import com.rejs.flashnote.domain.cards.dto.CardDto;
import com.rejs.flashnote.domain.cards.dto.request.CreateCardRequest;
import com.rejs.flashnote.domain.cards.dto.request.UpdateCardRequest;
import com.rejs.flashnote.domain.cards.service.CardService;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public String postCardCreate(
            @Valid @ModelAttribute("cardCreateRequest")CreateCardRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ){
        if(bindingResult.hasErrors()){
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "createCardRequest", bindingResult);
            redirectAttributes.addFlashAttribute("createCardRequest", request);
            return "redirect:/decks/" + request.getDeckId();
        }
        Long memberId = PrincipalUtils.getMemberId();
        Long cardId = cardService.createCard(memberId, request);
        return "redirect:/decks/" + request.getDeckId();
    }

    @PostMapping("/{id}/delete")
    public String postCardDelete(@PathVariable("id") Long id){
        Long deckId = cardService.deleteCard(id);
        return "redirect:/decks/" + deckId;
    }

    @GetMapping("/{id}/update")
    public String getCardUpdate(@PathVariable("id") Long id, Model model){
        CardDto cardDto = cardService.readById(id);
        model.addAttribute("deckId", cardDto.getDeckId());
        model.addAttribute("updateCardRequest", UpdateCardRequest.from(cardDto));
        return "cards/update";
    }

    @PostMapping("/{id}/update")
    public String postCardUpdate(@PathVariable("id") Long id,@Valid @ModelAttribute("updateCardRequest") UpdateCardRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "cards/update";
        }
        Long deckId = cardService.updateCard(request);
        return "redirect:/decks/" + deckId;
    }
}
