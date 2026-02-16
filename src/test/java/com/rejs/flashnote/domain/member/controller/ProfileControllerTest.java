package com.rejs.flashnote.domain.member.controller;

import com.navercorp.fixturemonkey.FixtureMonkey;
import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.common.test.TestDataBuilderGroup;
import com.rejs.flashnote.domain.member.dto.ProfileDto;
import com.rejs.flashnote.domain.member.dto.request.UpdateProfileRequest;
import com.rejs.flashnote.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.navercorp.fixturemonkey.api.expression.JavaGetterMethodPropertySelector.javaGetter;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    private final FixtureMonkey fixtureMonkey = TestDataBuilderGroup.fixtureMonkey();

    @Test
    @DisplayName("내 프로필 조회(GET /profile): members/profile 뷰 + myProfile=true + profile 모델 반환")
    @WithMockOidcMember
    void getMyProfile_success() throws Exception {
        // given
        Long memberId = 1L; // @WithMockOidcMember 기본 memberId가 1L이라고 가정 (프로젝트 설정에 맞춰 조정)
        ProfileDto profileDto = fixtureMonkey.giveMeBuilder(ProfileDto.class)
                .set(javaGetter(ProfileDto::getId), memberId)
                .sample();

        given(memberService.readProfile(memberId)).willReturn(profileDto);

        // when & then
        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/profile"))
                .andExpect(model().attribute("myProfile", true))
                .andExpect(model().attribute("profile", profileDto));

        then(memberService).should().readProfile(memberId);
    }

    @Test
    @DisplayName("내 프로필 수정 폼 조회(GET /profile/me/edit): members/edit 뷰 + profile/profileForm 모델 반환")
    @WithMockOidcMember
    void getMyProfileEdit_success() throws Exception {
        // given
        Long memberId = 1L;
        ProfileDto profileDto = fixtureMonkey.giveMeBuilder(ProfileDto.class)
                .set(javaGetter(ProfileDto::getId), memberId)
                .sample();

        given(memberService.readProfile(memberId)).willReturn(profileDto);

        // when & then
        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("members/edit"))
                .andExpect(model().attribute("profile", profileDto))
                .andExpect(model().attribute("profileForm", UpdateProfileRequest.from(profileDto)));

        then(memberService).should().readProfile(memberId);
    }

    @Test
    @DisplayName("내 프로필 수정(POST /profile/edit): 유효성 성공 시 수정 후 redirect:/members/me")
    @WithMockOidcMember
    void postMyProfileEdit_success() throws Exception {
        // given
        Long memberId = 1L;

        UpdateProfileRequest form = fixtureMonkey.giveMeBuilder(UpdateProfileRequest.class)
                .set("name", "new-name")
                .sample();

        ProfileDto updatedProfile = fixtureMonkey.giveMeBuilder(ProfileDto.class)
                .set(javaGetter(ProfileDto::getId), memberId)
                .sample();

        given(memberService.updateProfile(eq(memberId), any(UpdateProfileRequest.class)))
                .willReturn(updatedProfile);

        // when & then
        mockMvc.perform(post("/profile/edit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("profileForm", form)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        then(memberService).should().updateProfile(eq(memberId), any(UpdateProfileRequest.class));
    }

    @Test
    @DisplayName("내 프로필 수정(POST /profile/edit): 유효성 실패 시 members/edit로 돌아가고 profile을 다시 모델에 담는다")
    @WithMockOidcMember
    void postMyProfileEdit_validationFail() throws Exception {
        // given
        Long memberId = 1L;

        // name이 @NotBlank/@NotNull 등 검증 대상이라고 가정하고 실패 데이터 구성
        UpdateProfileRequest invalidForm = FixtureMonkey.create().giveMeBuilder(UpdateProfileRequest.class)
                .setNull(javaGetter(UpdateProfileRequest::getName))
                .sample();

        ProfileDto profileDto = fixtureMonkey.giveMeBuilder(ProfileDto.class)
                .set(javaGetter(ProfileDto::getId), memberId)
                .sample();

        given(memberService.readProfile(memberId)).willReturn(profileDto);

        // when & then
        mockMvc.perform(post("/profile/edit")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .flashAttr("profileForm", invalidForm)
                )
                .andExpect(status().isOk())
                .andExpect(view().name("members/edit"))
                .andExpect(model().attributeHasFieldErrors("profileForm", "name"))
                .andExpect(model().attribute("profile", profileDto));

        then(memberService).should().readProfile(memberId);
        then(memberService).shouldHaveNoMoreInteractions();
    }

    @Test
    @DisplayName("프로필 조회(GET /profile/{id}): 본인 프로필이면 myProfile=true")
    @WithMockOidcMember
    void getIdProfile_myProfileTrue() throws Exception {
        // given
        Long memberId = 1L;
        ProfileDto profileDto = fixtureMonkey.giveMeBuilder(ProfileDto.class)
                .set(javaGetter(ProfileDto::getId), memberId)
                .sample();

        given(memberService.readProfile(memberId)).willReturn(profileDto);

        // when & then
        mockMvc.perform(get("/profile/{id}", memberId))
                .andExpect(status().isOk())
                .andExpect(view().name("members/profile"))
                .andExpect(model().attribute("myProfile", true))
                .andExpect(model().attribute("profile", profileDto));

        then(memberService).should().readProfile(memberId);
    }

    @Test
    @DisplayName("프로필 조회(GET /profile/{id}): 타인 프로필이면 myProfile=false")
    @WithMockOidcMember
    void getIdProfile_myProfileFalse() throws Exception {
        // given
        Long myId = 1L;
        Long otherId = 99L;

        ProfileDto profileDto = fixtureMonkey.giveMeBuilder(ProfileDto.class)
                .set(javaGetter(ProfileDto::getId), otherId)
                .sample();

        given(memberService.readProfile(otherId)).willReturn(profileDto);

        // when & then
        mockMvc.perform(get("/profile/{id}", otherId))
                .andExpect(status().isOk())
                .andExpect(view().name("members/profile"))
                .andExpect(model().attribute("myProfile", false))
                .andExpect(model().attribute("profile", profileDto));

        then(memberService).should().readProfile(otherId);
    }
}