package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.NoteSummaryDto;
import com.rejs.flashnote.domain.note.dto.request.note.NoteEditRequest;
import com.rejs.flashnote.domain.note.dto.request.note.NoteSearchOption;
import com.rejs.flashnote.domain.note.service.NoteSearchService;
import com.rejs.flashnote.domain.note.service.NoteService;
import com.rejs.flashnote.global.controller.dto.Pagination;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final NoteSearchService noteSearchService;

    @PostMapping("/create")
    public String postNoteCreate(){
        Long memberId = PrincipalUtils.getMemberId();
        Long noteId = noteService.createNote(memberId);
        return "redirect:/notes/"+noteId + "/edit";
    }

    @GetMapping("/{id}")
    public String getNote(@PathVariable("id") Long noteId, Model model){
        NoteDto noteDto = noteService.readById(noteId);
        model.addAttribute("isOwner", Objects.equals(noteDto.getOwnerId(), PrincipalUtils.getMemberId()));
        model.addAttribute("note", noteDto);
        return "notes/id";
    }

    @GetMapping()
    public String getMyNotePage(
            @PageableDefault Pageable pageable,
            Model model
    ){
        Long memberId = PrincipalUtils.getMemberId();
        Page<NoteDto> results = noteService.readByPage(memberId, pageable);

        model.addAttribute("notes", results.getContent());
        model.addAttribute("pagination", Pagination.from(results).getPaginationMetadata());
        return "notes/page";
    }


    @GetMapping("/{id}/edit")
    public String getNoteEdit(@PathVariable("id") Long noteId, Model model){
        NoteDto noteDto = noteService.readById(noteId);
        model.addAttribute("noteId", noteId);
        model.addAttribute("noteForm", NoteEditRequest.from(noteDto));
        return "notes/edit";
    }

    @PostMapping("/{id}/edit")
    public String postNoteEdit(@PathVariable("id") Long noteId, @Valid @ModelAttribute("noteForm") NoteEditRequest request, BindingResult bindingResult, Model model){
        model.addAttribute("noteId", noteId);
        if(bindingResult.hasErrors()){
            return "notes/edit";
        }
        noteService.updateNote(noteId, request);
        return "redirect:/notes/"+noteId;
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable("id") Long noteId){
        noteService.deleteNote(noteId);
        return "redirect:/notes";
    }

    @Observed(name = "note.controller.search.public")
    @GetMapping("/search")
    public String searchPublicNote(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "TITLE_CONTENT") NoteSearchOption searchOption,
            @PageableDefault Pageable pageable,
            Model model
    ){
        Slice<NoteSummaryDto> results = noteSearchService.searchPublicNote(keyword, searchOption, pageable);

        model.addAttribute("notes", results.getContent());
        model.addAttribute("hasNext", results.hasNext());
        model.addAttribute("pageNumber", pageable.getPageNumber());
        model.addAttribute("pageSize", pageable.getPageSize());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchOption", searchOption);
        model.addAttribute("searchOptions", NoteSearchOption.values());
        return "notes/search";
    }
}
