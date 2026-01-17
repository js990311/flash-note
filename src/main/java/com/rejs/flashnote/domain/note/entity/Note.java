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
@SQLDelete(sql = "UPDATE notes SET deleted_at = NOW() WHERE note_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "notes")
public class Note extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "note_group_id")
    private NoteGroup group;

    public void edit(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static Note of(NoteGroup noteGroup, String title, String content){
        return Note.builder()
                .group(noteGroup)
                .title(title)
                .content(content)
                .build();
    }

    public static Note newNote(NoteGroup noteGroup){
        return Note.builder()
                .group(noteGroup)
                .title("새 노트")
                .content("")
                .build();
    }

}
