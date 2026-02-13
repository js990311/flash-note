package com.rejs.flashnote.global.meilisearch.template;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.meilisearch.sdk.Client;
import com.meilisearch.sdk.Index;
import com.meilisearch.sdk.SearchRequest;
import com.meilisearch.sdk.model.*;
import com.rejs.flashnote.global.meilisearch.document.DocumentMetadatas;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class MeilisearchTemplate {
    private final Client client;
    private final ObjectMapper objectMapper;

    public <T> Task waitForTask(Class<T> clazz, TaskInfo taskInfo){
        DocumentMetadatas documents = DocumentMetadatas.getByClazz(clazz);
        Index index = client.index(documents.getIndexName());
        index.waitForTask(taskInfo.getTaskUid());
        return index.getTask(taskInfo.getTaskUid());
    }

    public <T> Task waitForTask(Class<T> clazz, TaskInfo taskInfo, int timeoutInMs, int intervalInMs){
        DocumentMetadatas documents = DocumentMetadatas.getByClazz(clazz);
        Index index = client.index(documents.getIndexName());
        index.waitForTask(taskInfo.getTaskUid(), timeoutInMs, intervalInMs);
        return index.getTask(taskInfo.getTaskUid());
    }


    public <T> TaskInfo save(Class<T> clazz, T document){
        return this.saveAll(clazz, Collections.singletonList(document));
    }

    public <T> TaskInfo saveAll(Class<T> clazz, List<T> documents){
        try {
            DocumentMetadatas metadata = DocumentMetadatas.getByClazz(clazz);
            Index index = client.index(metadata.getIndexName());
            String jsonDocuments = objectMapper.writeValueAsString(documents);
            return index.addDocuments(jsonDocuments,metadata.getPrimarykey());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public <T> Page<T> search(Class<T> clazz, MeilisearchQuery query){
        try {
            DocumentMetadatas metadata = DocumentMetadatas.getByClazz(clazz);
            Index index = client.index(metadata.getIndexName());

            // 1. SearchRequest 생성
            SearchRequest request = new SearchRequest(query.getQuery());

            // 2. 필터 적용
            if (query.getFilter() != null && !query.getFilter().isBlank()) {
                request.setFilter(new String[]{query.getFilter()});
            }

            // 3. 검색 필드 제한 적용
            if (!query.getSearchAttributes().isEmpty()) {
                request.setAttributesToSearchOn(query.getSearchAttributes().toArray(new String[0]));
            }

            // 4. 페이징 및 정렬 적용
            Pageable pageable = query.getPageable();
            if (pageable.isPaged()) {
                request.setPage(pageable.getPageNumber() + 1);
                request.setHitsPerPage(pageable.getPageSize());
            }

            // 5. 실행
            SearchResultPaginated result = (SearchResultPaginated) index.search(request);

            // 6. 결과 매핑
            ArrayList<HashMap<String, Object>> hits = result.getHits();
            List<T> content = (hits == null || hits.isEmpty())
                    ? Collections.emptyList()
                    : hits.stream()
                    .map(hit -> objectMapper.convertValue(hit, clazz))
                    .collect(Collectors.toList());

            return new PageImpl<>(content, pageable, result.getTotalHits());

        } catch (Exception e) {
            throw new RuntimeException("Meilisearch search execution failed", e);
        }
    }

    public <T> Slice<T> searchSlice(Class<T> clazz, MeilisearchQuery query) {
        try {
            DocumentMetadatas metadata = DocumentMetadatas.getByClazz(clazz);
            Index index = client.index(metadata.getIndexName());

            // 1. SearchRequest 생성
            SearchRequest request = new SearchRequest(query.getQuery());

            // 2. 필터 적용
            if (query.getFilter() != null && !query.getFilter().isBlank()) {
                request.setFilter(new String[]{query.getFilter()});
            }

            // 3. 검색 필드 제한 적용
            if (!query.getSearchAttributes().isEmpty()) {
                request.setAttributesToSearchOn(query.getSearchAttributes().toArray(new String[0]));
            }

            // 프로젝션
            if (!query.getAttributesToRetrieve().isEmpty()) {
                request.setAttributesToRetrieve(query.getAttributesToRetrieve().toArray(new String[0]));
            }

            // 4. 페이징 및 정렬 적용
            Pageable pageable = query.getPageable();
            int pageSize = pageable.getPageSize();
            int offset = Math.toIntExact(pageable.getOffset());
            int limit = pageSize + 1;
            request.setOffset(offset);
            request.setLimit(limit);

            // 5. 실행
            Searchable search = index.search(request);

            // 6. 결과 매핑
            List<HashMap<String, Object>> hits = search.getHits();
            boolean hasNext = hits.size() > pageSize;
            if (hasNext) {
                hits = hits.subList(0, pageSize);
            }

            List<T> content = (hits == null || hits.isEmpty())
                    ? Collections.emptyList()
                    : hits.stream()
                    .map(hit -> objectMapper.convertValue(hit, clazz))
                    .collect(Collectors.toList());

            return new SliceImpl<>(content, pageable, hasNext);
        } catch (Exception e) {
            throw new RuntimeException("Meilisearch searchSlice execution failed", e);
        }
    }

    public <T, R> Slice<R> searchSlice(Class<T> clazz, MeilisearchQuery query, Class<R> returnClazz) {
        try {
            DocumentMetadatas metadata = DocumentMetadatas.getByClazz(clazz);
            Index index = client.index(metadata.getIndexName());

            // 1. SearchRequest 생성
            SearchRequest request = new SearchRequest(query.getQuery());

            // 2. 필터 적용
            if (query.getFilter() != null && !query.getFilter().isBlank()) {
                request.setFilter(new String[]{query.getFilter()});
            }

            // 3. 검색 필드 제한 적용
            if (!query.getSearchAttributes().isEmpty()) {
                request.setAttributesToSearchOn(query.getSearchAttributes().toArray(new String[0]));
            }

            // 프로젝션
            if (!query.getAttributesToRetrieve().isEmpty()) {
                request.setAttributesToRetrieve(query.getAttributesToRetrieve().toArray(new String[0]));
            }

            // 4. 페이징 및 정렬 적용
            Pageable pageable = query.getPageable();
            int pageSize = pageable.getPageSize();
            int offset = Math.toIntExact(pageable.getOffset());
            int limit = pageSize + 1;
            request.setOffset(offset);
            request.setLimit(limit);

            // 5. 실행
            Searchable search = index.search(request);

            // 6. 결과 매핑
            List<HashMap<String, Object>> hits = search.getHits();
            if(hits == null || hits.isEmpty()){
                return new SliceImpl<>(Collections.emptyList(), pageable, false);
            }
            boolean hasNext = hits.size() > pageSize;
            List<R> content = hits.stream()
                    .limit(pageSize)
                    .map(hit -> objectMapper.convertValue(hit, returnClazz))
                    .toList();
            return new SliceImpl<>(content, pageable, hasNext);
        } catch (Exception e) {
            throw new RuntimeException("Meilisearch searchSlice execution failed", e);
        }
    }



    public <T> Page<T> search(Class<T> clazz, SearchRequest request, Pageable pageable) {
        try {
            DocumentMetadatas metadata = DocumentMetadatas.getByClazz(clazz);
            Index index = client.index(metadata.getIndexName());

            request.setPage(pageable.getPageNumber() + 1);
            request.setHitsPerPage(pageable.getPageSize());

            SearchResultPaginated result = (SearchResultPaginated) index.search(request);
            ArrayList<HashMap<String, Object>> hits = result.getHits();
            List<T> content;

            if (hits == null || hits.isEmpty()) {
                content = Collections.emptyList();
            } else {
                content = hits.stream()
                        .map(hit -> objectMapper.convertValue(hit, clazz))
                        .collect(Collectors.toList());
            }
            return new PageImpl<>(content, pageable, result.getTotalHits());
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
