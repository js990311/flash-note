package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.service.NoteGroupService;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/note-groups")
public class NoteGroupController {
    private final NoteGroupService noteGroupService;

    @GetMapping("/create")
    public String getCreateNoteGroup(Model model){
        model.addAttribute("request", new CreateNoteGroupRequest());
        return "note-groups/create";
    }

    @PostMapping("/create")
    public String postCreateNoteGroup(@Valid @ModelAttribute("request") CreateNoteGroupRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "note-groups/create";
        }
        Long memberId = PrincipalUtils.getMemberId();
        noteGroupService.createNoteGroup(memberId, request);
        return "note-groups/create";
    }
}
