package com.rejs.flashnote.global.meilisearch.mapper;

import com.rejs.flashnote.domain.note.dto.NoteSummaryDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 부하테스트상에서 유의미한 성능개선을 이루지 못함
 */
public class MeilisearchMapper {

    private final Map<Class<?>, Function<Map<String, Object>, ?>> mappers = new ConcurrentHashMap<>();

    public MeilisearchMapper() {
        mappers.put(NoteSummaryDto.class, this::toNoteSummaryDto);
    }

    public <T> T map(Map<String, Object> hit, Class<T> clazz) {
        Function<Map<String, Object>, ?> mapper = mappers.get(clazz);
        if (mapper == null) {
            throw new IllegalArgumentException("해당 클래스에 대한 매퍼가 등록되지 않았습니다: " + clazz.getName());
        }
        return (T) mapper.apply(hit);
    }

    // ## NoteSummaryDto 전용 매핑
    private NoteSummaryDto toNoteSummaryDto(Map<String, Object> map) {
        return NoteSummaryDto.builder()
                .noteId(asLong(map.get("noteId")))
                .title((String) map.get("title"))
                .memberId(asLong(map.get("memberId")))
                .published(asBoolean(map.get("published")))
                .createdAt(asInstant(map.get("createdAt")))
                .updatedAt(asInstant(map.get("updatedAt")))
                .deletedAt(asInstant(map.get("deletedAt")))
                .build();
    }

    // ## 유틸리티

    private Long asLong(Object obj) {
        if (obj instanceof Number num) return num.longValue();
        if (obj instanceof String str) return Long.parseLong(str);
        return null;
    }

    private boolean asBoolean(Object obj) {
        if (obj instanceof Boolean bool) return bool;
        if (obj instanceof String str) return Boolean.parseBoolean(str);
        return false;
    }

    private Instant asInstant(Object obj) {
        if (obj == null) return null;
        // Meilisearch에서 ISO-8601 문자열로 올 경우
        if (obj instanceof String str) {
            try {
                return Instant.parse(str);
            } catch (Exception e) {
                return null;
            }
        }
        // 만약 숫자(Epoch Milli)로 올 경우
        if (obj instanceof Number num) {
            return Instant.ofEpochMilli(num.longValue());
        }
        return null;
    }
}