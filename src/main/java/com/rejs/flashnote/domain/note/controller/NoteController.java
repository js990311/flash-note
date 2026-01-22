package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.service.NoteService;
import com.rejs.flashnote.global.controller.dto.Pagination;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;

    @PostMapping("/create")
    public String postNoteCreate(){
        Long memberId = PrincipalUtils.getMemberId();
        Long noteId = noteService.createNote(memberId);
        return "redirect:/note/"+noteId + "/edit";
    }

    @GetMapping("/{id}")
    public String getNote(@PathVariable("id") Long noteId, Model model){
        NoteDto noteDto = noteService.readById(noteId);
        model.addAttribute("note", noteDto);
        return "note/id";
    }

    @GetMapping("/{id}")
    public String getNotePage(@PageableDefault Pageable pageable, Model model){
        Long memberId = PrincipalUtils.getMemberId();
        Page<NoteDto> noteDtos = noteService.readByPage(memberId, pageable);
        Pagination<NoteDto> notePage = Pagination.from(noteDtos);
        model.addAttribute("notes", notePage);
        return "note/page";
    }


    @GetMapping("/{id}/edit")
    public String getNoteEdit(@PathVariable("id") Long noteId, Model model){
        NoteDto noteDto = noteService.readById(noteId);
        model.addAttribute("noteForm", NoteEditRequest.from(noteDto));
        return "note/edit";
    }

    @PostMapping("/{id}/edit")
    public String postNoteEdit(@PathVariable("id") Long noteId, @Valid @ModelAttribute("noteForm") NoteEditRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "note/edit";
        }
        noteService.updateNote(noteId, request);
        return "redirect:/note/"+noteId;
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable("id") Long noteId){
        return "redirect:/";
    }
}
