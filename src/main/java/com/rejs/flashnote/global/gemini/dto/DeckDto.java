package com.rejs.flashnote.global.gemini.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeckDto {
    private List<CardDto> cards;
}
