package com.rejs.flashnote.domain.member.dto.request;

import com.rejs.flashnote.domain.member.dto.ProfileDto;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UpdateProfileRequest {
    @NotEmpty
    private String name;

    public static UpdateProfileRequest from(ProfileDto profile){
        return UpdateProfileRequest.builder()
                .name(profile.getName())
                .build();
    }
}
