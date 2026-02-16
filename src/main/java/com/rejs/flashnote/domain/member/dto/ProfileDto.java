package com.rejs.flashnote.domain.member.dto;

import com.rejs.flashnote.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private Long id;
    private String name;

    public static ProfileDto from(Member member){
        return ProfileDto.builder()
                .id(member.getId())
                .name(member.getName())
                .build();
    }
}
