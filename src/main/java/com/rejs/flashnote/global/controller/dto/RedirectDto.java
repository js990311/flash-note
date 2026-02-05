package com.rejs.flashnote.global.controller.dto;

import lombok.Getter;

@Getter
public class RedirectDto {
    private final String redirectUrl;

    public RedirectDto(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public static RedirectDto from(String redirectUrl){
        return new RedirectDto(redirectUrl);
    }
}
