package com.rejs.flashnote.domain.image.dto;

import com.rejs.flashnote.domain.image.entity.ImageMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class S3ViewMetadata {
    private Long id;
    private String originalFileName;
    private String contentType;
    private String viewUrl;
    private long size;

    public static S3ViewMetadata from(ImageMetadata entity) {
        return S3ViewMetadata.builder()
                .id(entity.getId())
                .originalFileName(entity.getFileName())
                .contentType(entity.getContentType())
                .viewUrl("/api/images/" + entity.getId())
                .size(entity.getFileSize())
                .build();
    }
}