package com.rejs.flashnote.domain.member.entity;

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
@SQLDelete(sql = "UPDATE members SET deleted_at = NOW() WHERE member_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "members")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column
    private String email;

    @Column
    private String name;

    @Column
    private String provider;

    @Enumerated(EnumType.STRING)
    @Column
    private MemberRole role;

    public static Member of(String email, String provider) {
        return Member.builder()
                .email(email)
                .provider(provider)
                .role(MemberRole.ROLE_USER)
                .name(email)
                .build();
    }
}
