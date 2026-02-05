package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class NoteRestController {
    private final NoteService noteService;

    @PostMapping("/{id}/edit")
    public String postNoteEdit(@PathVariable("id") Long noteId, @Valid @RequestBody NoteEditRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "notes/edit";
        }
        noteService.updateNote(noteId, request);
        return "redirect:/notes/"+noteId;
    }

}
