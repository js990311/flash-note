package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.dto.NoteGroupDto;
import com.rejs.flashnote.domain.note.dto.UpdateNoteGroupRequest;
import com.rejs.flashnote.domain.note.service.NoteGroupService;
import com.rejs.flashnote.global.controller.dto.Pagination;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        Pagination<NoteGroupDto> noteGroupDtos = Pagination.from(noteGroupService.readByPage(pageable));
        model.addAttribute("noteGroups", noteGroupDtos);
        return "note-groups/index";
    }

    @GetMapping("{id}/update")
    public String getUpdateNoteGroup(@PathVariable("id")Long noteGroupId, Model model){
        NoteGroupDto noteGroupDto = noteGroupService.readById(noteGroupId);
        model.addAttribute("id", noteGroupId);
        model.addAttribute("request", new UpdateNoteGroupRequest(noteGroupDto.getName()));
        return "note-groups/update";
    }

    @PostMapping("/{id}/update")
    public String postUpdateNoteGroup(@PathVariable("id")Long noteGroupId, @Valid @ModelAttribute("request") UpdateNoteGroupRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "note-groups/update";
        }
        noteGroupService.updateName(noteGroupId, request);
        return "redirect:/note-groups/"+ noteGroupId;
    }

    @PostMapping("/{id}/delete")
    public String postDeleteNoteGroup(@PathVariable("id")Long noteGroupId){
        noteGroupService.deleteNoteGroup(noteGroupId);
        return "redirect:/note-groups";
    }
}
