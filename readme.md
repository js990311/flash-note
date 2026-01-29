```mermaid
sequenceDiagram
    participant JSP as 브라우저
    participant Controller as Spring Controller
    participant Service as Gemini Service (@Async)
    participant DB as Database

    JSP->>Controller: 노트를 플래시카드로 만들기

    activate Controller
    Controller->>DB: Deck 객체 생성 (status: 'PROCESSING')
    DB-->>Controller: deckId 반환

    Controller->>Service: 비동기 호출 process(deckId, content)
    activate Service
    Note over Service: @Async 쓰레드에서 분리되어 실행

    Controller-->>JSP: 생성된 deckId로 리다이렉트
    deactivate Controller

    JSP->>JSP : 상태가 AI_GENERATING 경우 폴링 로직 실행
    Loop 3~5초 간격 폴링
        JSP->>Controller: 풀링
        Controller->>DB: 상태 조회
        DB-->>Controller: 'AI_GENERATING' 반환
    end

    Service->>Service:  GEMINI에게 플래시카드 생성 요청 및 응답처리
    Service->>DB: 결과 저장 및 상태 변경 (status: 'COMPLETED')
    deactivate Service

    Loop 작업 완료 후 폴링
        Controller->>DB: 상태 조회
        DB-->>Controller: 'COMPLETED' 반환
    end

```

````mermaid
stateDiagram-v2
    [*] --> AI_GENERATING : 1. AI 루트 (AI에 의해 생성 중)
    [*] --> COMPLETED : 2. 수동 루트 (즉시 생성 완료)

    AI_GENERATING --> COMPLETED : AI 작업 성공
    AI_GENERATING --> AI_GEN_FAILED : AI 생성 실패

    AI_GEN_FAILED --> AI_GENERATING : 재시도 시
    COMPLETED --> [*] : 사용자에게 노출```
````
