package com.rejs.flashnote.domain.decks.controller;

import com.rejs.flashnote.domain.decks.service.generate.GenerateDeckFacade;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
public class GenerateDeckController {
    private GenerateDeckFacade generateDeckFacade;

    @PostMapping("/notes/{id}/generate")
    public String postGenerate(@PathVariable("id") Long noteId){
        Long memberId = PrincipalUtils.getMemberId();
        Long deckId = generateDeckFacade.generateFlashCardFromNoteId(noteId, memberId);
        return "redirect:/decks/" + deckId;
    }

}
