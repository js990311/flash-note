package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.request.NoteEditRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/note")
public class NoteController {
    @GetMapping("/edit")
    public String getNoteEdit(Model model){
        model.addAttribute("noteForm", new NoteEditRequest());
        return "note/edit";
    }

    @PostMapping("/edit")
    public String postNoteEdit(@Valid @ModelAttribute("noteForm") NoteEditRequest request, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "note/edit";
        }
        log.info(request.getTitle());
        log.info(request.getContent());
        model.addAttribute("noteForm", request);
        return "note/edit";
    }
}
