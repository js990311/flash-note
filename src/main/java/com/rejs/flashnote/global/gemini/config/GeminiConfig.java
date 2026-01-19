package com.rejs.flashnote.global.gemini.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.rejs.flashnote.global.gemini.context.PersonaContext;
import com.rejs.flashnote.global.gemini.context.PersonaContextRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class GeminiConfig {
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("classpath:gemini/prompts/persona.txt")
    private Resource chefSystemResource;

    @Value("classpath:gemini/schemas/flashcard.json")
    private Resource recipeSchemaResource;

    @Bean
    public PersonaContextRegistry personaContextRegistry(){
        try {
            log.info("[GeminiConfig] Initializing PersonaContextRegistry");

            String personaText = StreamUtils.copyToString(
                    chefSystemResource.getInputStream(),
                    StandardCharsets.UTF_8
            );

            Map<String, Object> schemaMap = objectMapper.readValue(
                    recipeSchemaResource.getInputStream(),
                    new TypeReference<Map<String, Object>>() {}
            );

            PersonaContext flashcardContext = new PersonaContext(personaText, schemaMap);

            Map<String, PersonaContext> contextMap = new ConcurrentHashMap<>();
            contextMap.put("FLASHCARD", flashcardContext);

            return new PersonaContextRegistry(contextMap);
        } catch (IOException e) {
            log.error("Failed to initialize PersonaContextRegistry", e);
            throw new RuntimeException("Failed to initialize PersonaContextRegistry", e);
        }
    }

    @Bean
    public Client client(){
        return Client.builder()
                .apiKey(geminiApiKey)
                .build();
    }
}
