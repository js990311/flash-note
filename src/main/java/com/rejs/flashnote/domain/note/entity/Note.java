package com.rejs.flashnote.domain.note.entity;

import com.rejs.flashnote.domain.member.entity.Member;
import com.rejs.flashnote.global.repository.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
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
    @Tsid
    @Column(name = "note_id")
    private Long id;

    @Column
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column
    private boolean published = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void edit(String title, String content, boolean published) {
        this.title = title;
        this.content = content;
        this.published = published;
    }

    public static Note of(Member member, String title, String content, boolean published){
        return Note.builder()
                .member(member)
                .title(title)
                .content(content)
                .published(published)
                .build();
    }

    public static Note newNote(Member member){
        return Note.builder()
                .member(member)
                .title("새 노트")
                .content("")
                .build();
    }

}
