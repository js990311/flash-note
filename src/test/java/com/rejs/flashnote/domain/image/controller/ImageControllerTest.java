package com.rejs.flashnote.domain.image.controller;

import com.rejs.flashnote.common.security.WithMockOidcMember;
import com.rejs.flashnote.domain.image.dto.ImageWithMetadata;
import com.rejs.flashnote.domain.image.dto.S3ViewMetadata;
import com.rejs.flashnote.domain.image.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ImageController.class)
class ImageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Test
    @WithMockOidcMember
    @DisplayName("POST /api/images - 업로드 요청 시 id 반환")
    void uploadImage_returnsId() throws Exception {
        // given
        long memberId = 1L;
        long imageId = 123L;

        given(imageService.uploadImageReturnId(any(), eq(memberId)))
                .willReturn(imageId);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/api/images")
                                .file("file", "dummy".getBytes())
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value((int) imageId));
    }

    @Test
    @WithMockOidcMember
    @DisplayName("GET /api/images/{id} - contentType/size 헤더 포함하여 이미지 리턴")
    void getImage_returnsResourceWithHeaders() throws Exception {
        // given
        long id = 123L;
        String contentType = "image/png";
        byte[] bytes = new byte[]{1, 2, 3, 4, 5};

        S3ViewMetadata metadata = S3ViewMetadata.builder()
                .id(id)
                .originalFileName("a.png")
                .contentType(contentType)
                .s3Key("images/123.png")
                .size(bytes.length)
                .build();

        ByteArrayResource resource = new ByteArrayResource(bytes);

        given(imageService.getImageResourceWithMetadata(eq(id)))
                .willReturn(new ImageWithMetadata(metadata, resource));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/images/{id}", id))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", contentType))
                .andExpect(header().longValue("Content-Length", bytes.length))
                .andExpect(content().bytes(bytes));
    }

}