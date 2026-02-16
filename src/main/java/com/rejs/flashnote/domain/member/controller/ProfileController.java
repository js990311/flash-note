package com.rejs.flashnote.domain.member.controller;

import com.rejs.flashnote.domain.member.dto.ProfileDto;
import com.rejs.flashnote.domain.member.dto.request.UpdateProfileRequest;
import com.rejs.flashnote.domain.member.service.MemberService;
import com.rejs.flashnote.domain.note.dto.NoteDto;
import com.rejs.flashnote.domain.note.dto.NoteSummaryDto;
import com.rejs.flashnote.domain.note.service.NoteService;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/profile")
@Controller
public class ProfileController {
    private final MemberService memberService;
    private final NoteService noteService;

    @GetMapping
    public String getMyProfile(Model model){
        Long memberId = PrincipalUtils.getMemberId();
        ProfileDto profileDto = memberService.readProfile(memberId);
        model.addAttribute("myProfile", true);
        model.addAttribute("profile", profileDto);
        List<NoteSummaryDto> notes = noteService.readProfilePage(memberId, false);
        model.addAttribute("notes", notes);
        return "members/profile";
    }

    @GetMapping("/edit")
    public String getMyProfileEdit(Model model) {
        Long memberId = PrincipalUtils.getMemberId();
        ProfileDto profile = memberService.readProfile(memberId);

        model.addAttribute("profile", profile);
        model.addAttribute("profileForm", UpdateProfileRequest.from(profile));
        return "members/edit";
    }

    @PostMapping("/edit")
    public String postMyProfileEdit(
            @Valid @ModelAttribute("profileForm") UpdateProfileRequest request,
            BindingResult bindingResult,
            Model model
    ) {
        Long memberId = PrincipalUtils.getMemberId();
        if (bindingResult.hasErrors()) {
            ProfileDto profile = memberService.readProfile(memberId);
            model.addAttribute("profile", profile);
            return "members/edit";
        }
        memberService.updateProfile(memberId, request);
        return "redirect:/profile";
    }

    @GetMapping("/{id}")
    public String getIdProfile(
            @PathVariable("id") Long memberId,
            Model model
    ){
        ProfileDto profileDto = memberService.readProfile(memberId);
        model.addAttribute("myProfile", PrincipalUtils.getMemberId().equals(memberId));
        model.addAttribute("profile", profileDto);
        List<NoteSummaryDto> notes = noteService.readProfilePage(memberId, true);
        model.addAttribute("notes", notes);
        return "members/profile";
    }
}
