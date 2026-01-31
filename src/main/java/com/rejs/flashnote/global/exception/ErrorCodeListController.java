package com.rejs.flashnote.global.exception;

import com.rejs.flashnote.domain.cards.error.CardErrorCode;
import com.rejs.flashnote.domain.decks.error.DeckErrorCode;
import com.rejs.flashnote.domain.member.error.MemberErrorCode;
import com.rejs.flashnote.domain.note.error.NoteErrorCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/error/docs")
public class ErrorCodeListController {
    private final List<ErrorCode> errorCodes;

    public ErrorCodeListController() {
        this.errorCodes = new ArrayList<>();
        errorCodes.addAll(List.of(CommonErrorCode.values()));
        errorCodes.addAll(List.of(MemberErrorCode.values()));
        errorCodes.addAll(List.of(NoteErrorCode.values()));
        errorCodes.addAll(List.of(DeckErrorCode.values()));
        errorCodes.addAll(List.of(CardErrorCode.values()));
    }

    @GetMapping
    public String getErrorList(Model model){
        model.addAttribute("errorCodes", errorCodes);
        return "error/list";
    }
}
