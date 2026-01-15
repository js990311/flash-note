package com.rejs.flashnote.domain.note.dto;

import java.time.LocalDateTime;

public class NoteGroupListDto {
    /**
     * noteGroup의 id
     */
    private Long id;
    /**
     * noteGroup의 이름
     */
    private String groupName;
    /**
     * 사용자가 noteGroup에 대해서 가지고 있는 권한
     */
    private String permission;
    /**
     * 최근 수정일자
     */
    private LocalDateTime updatedAt;

    public NoteGroupListDto(Long id, String groupName, String permission, LocalDateTime updatedAt) {
        this.id = id;
        this.groupName = groupName;
        this.permission = permission;
        this.updatedAt = updatedAt;
    }
}
