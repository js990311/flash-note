package com.rejs.flashnote.global.gemini.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.rejs.flashnote.global.gemini.context.PersonaContext;
import com.rejs.flashnote.global.gemini.context.PersonaContextRegistry;
import com.rejs.flashnote.global.gemini.dto.GeneratedDeckDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class GeminiService {
    private final PersonaContextRegistry personaContextRegistry;
    private final Client client;
    private final ObjectMapper objectMapper;

    public GeneratedDeckDto readCards(String userPrompt){
        try {
            PersonaContext context = personaContextRegistry.getContext("FLASHCARD");
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(Content.fromParts(Part.fromText(context.getPersona())))
                    .responseMimeType("application/json")
                    .responseJsonSchema(context.getSchema())
                    .temperature(1.0f)
                    .build();

            GenerateContentResponse response = client.models.generateContent(
                    "gemini-2.5-flash-lite",
                    userPrompt,
                    config
            );

            String jsonResult = response.text();
            return objectMapper.readValue(jsonResult, GeneratedDeckDto.class);
        }catch (Exception e){
            log.error("플래시카드 생성 중 오류 발생", e);
            throw new RuntimeException("AI 응답 처리 실패", e);
        }
    }
}
