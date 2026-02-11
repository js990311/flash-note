package com.rejs.flashnote.global.meilisearch.config.initializer;

import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.model.Settings;
import com.meilisearch.sdk.model.TaskInfo;
import com.rejs.flashnote.global.meilisearch.config.initializer.annotation.Filterable;
import com.rejs.flashnote.global.meilisearch.document.DocumentMetadatas;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeilisearchInitializer {
    private final Client client;

    @PostConstruct
    public void initIndexes() {
        for (DocumentMetadatas metadata : DocumentMetadatas.values()) {
            applySettingsFor(metadata);
        }
    }

    private void applySettingsFor(DocumentMetadatas metadata) {
        try {
            String indexName = metadata.getIndexName();
            Class<?> clazz = metadata.getClazz();

            // 1. 인덱스가 없으면 생성 (getOrCreateIndex 로직)
            Index index = client.index(indexName);

            // 2. 클래스 필드를 스캔하여 설정값 추출
            Settings settings = new Settings();

            // 필터링 속성 추출 (@Filterable)
            List<String> filterableAttributes = getFieldNamesByAnnotation(clazz, Filterable.class);
            settings.setFilterableAttributes(filterableAttributes.toArray(new String[0]));

            // 3. 설정 업데이트 (비동기)
            TaskInfo task = index.updateSettings(settings);

            log.debug("[Meilisearch] Index '{}' settings updated. TaskUID: {}", indexName, task.getTaskUid());
        } catch (Exception e) {
            log.error("[Meilisearch] Failed to init index: {}", metadata.getIndexName(), e);
        }
    }

    // 리플렉션을 사용하여 특정 어노테이션이 붙은 필드 이름 추출
    private List<String> getFieldNamesByAnnotation(Class<?> clazz, Class<? extends java.lang.annotation.Annotation> annotationClass) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotationClass)) {
                fieldNames.add(field.getName());
            }
        }
        return fieldNames;
    }
}
