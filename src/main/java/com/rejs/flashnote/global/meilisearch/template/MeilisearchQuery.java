package com.rejs.flashnote.global.meilisearch.template;

import com.meilisearch.sdk.SearchRequest;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
public class MeilisearchQuery {
    private final String query;
    private final String filter;
    @Builder.Default
    private final List<String> searchAttributes = new ArrayList<>();
    private final Pageable pageable;
}
