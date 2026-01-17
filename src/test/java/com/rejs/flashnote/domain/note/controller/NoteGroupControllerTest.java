package com.rejs.flashnote.domain.note.controller;

import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.domain.note.dto.request.group.CreateNoteGroupRequest;
import com.rejs.flashnote.domain.note.dto.NoteGroupDto;
import com.rejs.flashnote.domain.note.dto.request.group.UpdateNoteGroupRequest;
import com.rejs.flashnote.domain.note.service.NoteGroupService;
import com.rejs.flashnote.domain.note.service.NoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteGroupController.class)
class NoteGroupControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteGroupService noteGroupService;
    @MockitoBean
    private NoteService noteService;

    @Test
    @WithMockOidcMember
    @DisplayName("생성 페이지 요청 시 빈 request 객체를 모델에 담아 뷰를 반환한다")
    void getCreateNoteGroup_Success() throws Exception {
        mockMvc.perform(get("/note-groups/create"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(view().name("note-groups/create"));
    }

    @Test
    @WithMockOidcMember
    @DisplayName("올바른 폼 데이터 전송 시 노트 그룹을 생성하고 리다이렉트한다")
    void postCreateNoteGroup_Success() throws Exception {
        // given
        Long mockMemberId = 1L;

        Long mockId = 123L;
        given(noteGroupService.createNoteGroup(anyLong(), any(CreateNoteGroupRequest.class))).willReturn(mockId);

        // when & then
        mockMvc.perform(post("/note-groups/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "나의 첫 노트 그룹")
                        .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/note-groups/" + mockId));

        // 서비스가 올바른 인자로 호출되었는지 검증
        verify(noteGroupService, times(1)).createNoteGroup(eq(mockMemberId), any(CreateNoteGroupRequest.class));
    }

    @Test
    @WithMockOidcMember
    @DisplayName("이름이 비어있는 경우 검증 에러가 발생하고 다시 폼 페이지를 보여준다")
    void postCreateNoteGroup_ValidationError() throws Exception {
        // when & then
        mockMvc.perform(post("/note-groups/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "")
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("request", "name"))
                .andExpect(view().name("note-groups/create"));

        // 에러 시 서비스가 호출되지 않아야 함
        verify(noteGroupService, never()).createNoteGroup(any(), any());
    }

    @Test
    @WithMockOidcMember
    @DisplayName("수정 페이지 요청 시 기존 데이터를 조회하여 모델에 담아 뷰를 반환한다")
    void getUpdateNoteGroup_Success() throws Exception {
        // given
        Long noteGroupId = 1L;
        NoteGroupDto mockDto = new NoteGroupDto(noteGroupId, "기존 그룹명");
        given(noteGroupService.readById(noteGroupId)).willReturn(mockDto);

        // when & then
        mockMvc.perform(get("/note-groups/{id}/update", noteGroupId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(view().name("note-groups/update"));

        verify(noteGroupService, times(1)).readById(noteGroupId);
    }

    @Test
    @WithMockOidcMember
    @DisplayName("수정 폼 데이터 전송 시 성공적으로 수정하고 상세 페이지로 리다이렉트한다")
    void postUpdateNoteGroup_Success() throws Exception {
        // given
        Long noteGroupId = 1L;
        String updatedName = "수정된 그룹명";

        // when & then
        mockMvc.perform(post("/note-groups/{id}/update", noteGroupId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", updatedName)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/note-groups/" + noteGroupId));

        verify(noteGroupService, times(1)).updateName(eq(noteGroupId), any(UpdateNoteGroupRequest.class));
    }

    @Test
    @WithMockOidcMember
    @DisplayName("수정 시 검증 에러가 발생하면 다시 수정 페이지를 보여준다")
    void postUpdateNoteGroup_ValidationError() throws Exception {
        // given
        Long noteGroupId = 1L;

        // when & then
        mockMvc.perform(post("/note-groups/{id}/update", noteGroupId)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "") // 빈 값 전송 (Validation 에러 유발)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(view().name("note-groups/update"));

        verify(noteGroupService, never()).updateName(any(), any());
    }

    @Test
    @WithMockOidcMember
    @DisplayName("삭제 요청 시 해당 노트를 삭제하고 목록 페이지로 리다이렉트한다")
    void postDeleteNoteGroup_Success() throws Exception {
        // given
        Long noteGroupId = 1L;

        // when & then
        mockMvc.perform(post("/note-groups/{id}/delete", noteGroupId)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/note-groups"));

        verify(noteGroupService, times(1)).deleteNoteGroup(noteGroupId);
    }
}