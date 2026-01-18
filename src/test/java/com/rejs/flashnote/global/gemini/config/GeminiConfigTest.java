package com.rejs.flashnote.global.gemini.config;

import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.global.gemini.context.PersonaContext;
import com.rejs.flashnote.global.gemini.context.PersonaContextRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@SpringBootTest
class GeminiConfigTest {

    @Autowired
    private PersonaContextRegistry personaContextRegistry;

    @Test
    @DisplayName("설정 파일들이 정상적으로 로드되어 레지스트리에 등록되어야 한다")
    void contextLoadTest() {
        // 1. Registry 빈 자체가 null이 아니어야 함
        assertThat(personaContextRegistry).isNotNull();

        // 2. "FLASHCARD" 키로 컨텍스트를 조회할 수 있어야 함
        PersonaContext context = personaContextRegistry.getContext("FLASHCARD");
        assertThat(context).isNotNull();

        // 3. 내용물 검증 (파일이 비어있지 않은지)
        assertThat(context.getPersona()).isNotBlank(); // persona.txt 내용 확인
        assertThat(context.getSchema()).isNotEmpty();  // flashcard.json 파싱 확인

        System.out.println("Loaded Persona: " + context.getPersona().substring(0, Math.min(20, context.getPersona().length())) + "...");
        System.out.println("Loaded Schema Keys: " + context.getSchema().keySet());
    }}