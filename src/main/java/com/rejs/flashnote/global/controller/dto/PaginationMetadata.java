package com.rejs.flashnote.global.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMetadata {
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int pageSize;
    private int startPage;
    private int endPage;
    private boolean hasPrevious;
    private boolean hasNext;

    public static <T> PaginationMetadata from(Page<T> page) {
        int blockLimit = 10;
        int current = page.getNumber() + 1;
        int total = page.getTotalPages();

        int start = (((int) Math.ceil((double) current / blockLimit)) - 1) * blockLimit + 1;
        int end = Math.min((start + blockLimit - 1), total == 0 ? 1 : total);
        return
                PaginationMetadata.builder()
                .currentPage(current)
                .totalPages(total)
                .totalElements(page.getTotalElements())
                .pageSize(page.getSize())
                .startPage(start)
                .endPage(end)
                .hasPrevious(start > 1)
                .hasNext(end < total)
                .build();
    }
}
