package com.rejs.flashnote.global.gemini.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.rejs.flashnote.TestcontainersConfiguration;
import com.rejs.flashnote.global.gemini.context.PersonaContext;
import com.rejs.flashnote.global.gemini.context.PersonaContextRegistry;
import com.rejs.flashnote.global.gemini.dto.DeckDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * 평상시에는 실행하지 말것.
 * api 사용량 쓰니까...
 */
@Disabled
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class GeminiServiceTest {

    @Autowired
    private GeminiService geminiService;

    @Test
    @DisplayName("실제 Gemini API를 호출하여 플래시카드 객체를 생성한다")
    void readCards_IntegrationTest() {
        // Given
        String userPrompt = """
                    # 자바(Java) 핵심 요약 정리
                    
                    ## 1. 자바의 특징과 JVM
                    - **Write Once, Run Anywhere (WORA):** 플랫폼 독립성.
                    - **JVM (Java Virtual Machine):** 바이트코드(.class)를 실행하는 엔진.
                      - *컴파일 과정:* .java -> 자바 컴파일러(javac) -> .class(바이트코드) -> JVM 인터프리터 & JIT 컴파일러 -> 기계어.
                    
                    ## 2. 데이터 타입 (Data Types)
                    ### 기본형 (Primitive Types)
                    - 정수형: byte(1), short(2), int(4), long(8)
                    - 실수형: float(4), double(8)
                    - 기타: boolean(1), char(2, 유니코드)
                    
                    ### 참조형 (Reference Types)
                    - 객체의 **주소값**을 저장 (Heap 메모리 영역 참조).
                    - 예: String, Array, 모든 클래스 객체.
                    
                    ## 3. 객체지향 프로그래밍 (OOP)의 4요소
                    1. **캡슐화 (Encapsulation):** 정보 은닉. `private` 접근 제어자와 `getter/setter` 사용.
                    2. **상속 (Inheritance):** 기존 클래스 재사용. `extends` 키워드.
                    3. **추상화 (Abstraction):** 공통 특성 추출. `abstract` 클래스 및 `interface`.
                    4. **다형성 (Polymorphism):** 한 객체가 여러 타입을 가짐. 오버로딩(Overloading)과 오버라이딩(Overriding).
                    
                    ## 4. 메모리 구조 (JVM Runtime Data Area)
                    - **Stack:** 지역 변수, 매개 변수가 저장되며 메서드 종료 시 소멸.
                    - **Heap:** `new`로 생성된 객체(인스턴스)가 저장됨. 가비지 컬렉터(GC)의 관리 대상.
                    - **Method Area:** 클래스 정보, static 변수가 저장됨.
                """;

        // When
        // 실제 구글 서버와 통신하므로 네트워크 상태에 따라 1~3초 정도 소요될 수 있습니다.
        DeckDto result = geminiService.readCards(userPrompt);

        // Then
        // 1. 결과 객체가 null이 아닌지 확인
        assertNotNull(result, "응답 객체는 null일 수 없습니다.");

        // 2. 리스트가 비어있지 않은지 확인
        assertNotNull(result.getCards(), "카드 리스트가 존재해야 합니다.");
        assertFalse(result.getCards().isEmpty(), "최소 하나 이상의 카드가 생성되어야 합니다.");

        // 3. 데이터 품질 확인 (구조화된 출력 검증)
        result.getCards().forEach(card -> {
            assertNotNull(card.getFront(), "카드 앞면 내용이 없습니다.");
            assertNotNull(card.getBack(), "카드 뒷면 내용이 없습니다.");

            System.out.println("-------------------------");
            System.out.println("Front: " + card.getFront());
            System.out.println("Back: " + card.getBack());
        });

        // 4. 요청한 개수와 일치하는지 확인 (프롬프트 반영 여부)
        assertTrue(result.getCards().size() >= 1);
    }
}