package com.rejs.flashnote.domain.note.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 해당 노트그룹에 접근할 권한을 정의함
 */
@AllArgsConstructor
@Getter
public enum NoteRole {
    VIEWER(null), // 읽기 가능
    EDITOR(VIEWER), // 수정가능
    MANAGER(EDITOR), // 노트그룹의 권한 부여 가능
    OWNER(MANAGER) // 당연히 주인은 모든 권한 보유
    ;

    /**
     * 권한 간 계층구조를 위한 디자인
     */
    private final NoteRole parent;

    public boolean hasPermission(NoteRole target){
        if (this == target){
            return true;
        }

        if(parent == null){
            return false;
        }

        return parent.hasPermission(target);
    }
}
