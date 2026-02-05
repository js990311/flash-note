package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.service.NoteService;
import com.rejs.flashnote.global.controller.dto.RedirectDto;
import com.rejs.flashnote.global.exception.throwable.InvalidParameterException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class NoteRestController {
    private final NoteService noteService;

    @PostMapping("/api/note/{id}/edit")
    public RedirectDto postNoteEdit(@PathVariable("id") Long noteId, @Valid @RequestBody NoteEditRequest request, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw InvalidParameterException.from(bindingResult);
        }
        noteService.updateNote(noteId, request);
        return RedirectDto.from("/notes/" +noteId);
    }

}
