package com.rejs.flashnote.global.gemini.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.errors.GenAiIOException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import com.rejs.flashnote.global.gemini.context.PersonaContext;
import com.rejs.flashnote.global.gemini.context.PersonaContextRegistry;
import com.rejs.flashnote.global.gemini.dto.GeneratedDeckDto;
import com.rejs.flashnote.global.gemini.exception.GeminiErrorCode;
import com.rejs.flashnote.global.gemini.exception.GeminiServiceException;
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
            if (context == null) {
                throw new GeminiServiceException(GeminiErrorCode.CONTEXT_NOT_FOUND);
            }
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
        } catch (GeminiServiceException e) {
            throw e;
        }catch (JsonProcessingException e) {
            log.error("[GeminiService] JSON parsing failed", e);
            throw new GeminiServiceException(GeminiErrorCode.RESPONSE_PARSING_FAILED, e);
        } catch (Exception e){
            GeminiErrorCode errorCode = resolveErrorCode(e);
            log.error("[GeminiService] Gemini API call failed. code={}, message={}", errorCode, e.getMessage(), e);
            throw new GeminiServiceException(errorCode, e);        }
    }

    private GeminiErrorCode resolveErrorCode(Exception e) {
        if (e instanceof ApiException apiException) {
            // 400 / 500을 포함함
            int statusCode = apiException.code();
            return switch (statusCode) {
                case 400 -> GeminiErrorCode.INVALID_REQUEST;
                case 401 -> GeminiErrorCode.UNAUTHORIZED;
                case 403 -> GeminiErrorCode.PERMISSION_DENIED;
                case 408, 504 -> GeminiErrorCode.UPSTREAM_TIMEOUT;
                case 429 -> GeminiErrorCode.RATE_LIMIT;
                case 500, 502, 503 -> GeminiErrorCode.UPSTREAM_UNAVAILABLE;
                default -> GeminiErrorCode.UNKNOWN;
            };
        }else if (e instanceof GenAiIOException) {
            // 네트워크 연결 실패 등
            return GeminiErrorCode.UPSTREAM_TIMEOUT;
        }
        return GeminiErrorCode.UNKNOWN;
    }

}
