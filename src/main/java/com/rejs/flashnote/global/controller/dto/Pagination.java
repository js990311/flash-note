package com.rejs.flashnote.global.controller.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class Pagination <T>{
    private List<T> contents;
    private PaginationMetadata paginationMetadata;

    public Pagination(List<T> contents, PaginationMetadata paginationMetadata) {
        this.contents = contents;
        this.paginationMetadata = paginationMetadata;
    }

    public static <T> Pagination<T> from(Page<T> page){
        return new Pagination<>(
                page.getContent(),
                PaginationMetadata.from(page)
        );
    }
}
