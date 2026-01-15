package com.rejs.flashnote.domain.note.entity;

import com.rejs.flashnote.global.repository.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
@SQLDelete(sql = "UPDATE note_groups SET deleted_at = NOW() WHERE note_group_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "note_groups")
public class NoteGroup extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_group_id")
    private Long id;

    @Column
    private String name;
}
