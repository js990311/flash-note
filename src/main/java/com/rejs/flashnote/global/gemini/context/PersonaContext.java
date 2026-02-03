package com.rejs.flashnote.global.gemini.context;

import lombok.Getter;

import java.util.Map;

@Getter
public class PersonaContext {
    private final String persona;
    private final Map<String, Object> schema;

    public PersonaContext(String persona, Map<String, Object> schema) {
        this.persona = persona;
        this.schema = schema;
    }
}
