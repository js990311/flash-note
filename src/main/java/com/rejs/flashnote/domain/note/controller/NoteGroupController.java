package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.dto.NoteGroupDto;
import com.rejs.flashnote.domain.note.service.NoteGroupService;
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
        Long noteGroupId = noteGroupService.createNoteGroup(memberId, request);
        return "redirect:/note-groups/"+ noteGroupId;
    }

    @GetMapping("/{id}")
    public String getNoteGroupById(@PathVariable("id") Long id, Model model){
        NoteGroupDto noteGroupDto = noteGroupService.readById(id);
        model.addAttribute("noteGroup", noteGroupDto);
        return "note-groups/id";
    }

    @GetMapping
    public String getNoteGroupIndex(@PageableDefault Pageable pageable, Model model){
        Page<NoteGroupDto> noteGroupDtos = noteGroupService.readByPage(pageable);
        model.addAttribute("noteGroups", noteGroupDtos);
        return "note-groups/index";
    }

}
