package com.rejs.flashnote.global.gemini.dto;

import lombok.Data;

import java.util.List;

@Data
public class GeneratedDeckDto {
    private List<GeneratedCardDto> cards;
}
