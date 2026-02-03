package com.rejs.flashnote.global.gemini.context;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class PersonaContextRegistry {
    private final Map<String, PersonaContext> contexts;

    public PersonaContext getContext(String name){
        return contexts.get(name);
    }
}
