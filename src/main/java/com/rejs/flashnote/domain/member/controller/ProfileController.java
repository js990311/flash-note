package com.rejs.flashnote.domain.member.controller;

import com.rejs.flashnote.domain.member.dto.ProfileDto;
import com.rejs.flashnote.domain.member.dto.request.UpdateProfileRequest;
import com.rejs.flashnote.domain.member.service.MemberService;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/profile")
@Controller
public class ProfileController {
    private final MemberService memberService;

    @GetMapping("")
    public String getMyProfile(Model model){
        Long memberId = PrincipalUtils.getMemberId();
        ProfileDto profileDto = memberService.readProfile(memberId);
        model.addAttribute("myProfile", true);
        model.addAttribute("profile", profileDto);
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
        return "members/profile";
    }
}
