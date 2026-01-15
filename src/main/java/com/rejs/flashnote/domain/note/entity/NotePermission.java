package com.rejs.flashnote.domain.note.entity;

import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.global.repository.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.userdetails.User;

/**
 * 말이 NotePermission이지만 실제로는 NoteGroup에 대한 인가처리를 위한 객체
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@SQLDelete(sql = "UPDATE note_permissions SET deleted_at = NOW() WHERE note_permission_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "note_permissions")
public class NotePermission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_permission_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private NoteRole role;

    @ManyToOne
    @JoinColumn(name = "note_group_id")
    private NoteGroup noteGroup;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
