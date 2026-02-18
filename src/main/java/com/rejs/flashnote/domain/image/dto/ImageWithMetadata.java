package com.rejs.flashnote.domain.image.dto;

import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
public class ImageWithMetadata {
    private S3ViewMetadata metadata;
    private Resource resource;

    public ImageWithMetadata(S3ViewMetadata metadata, Resource resource) {
        this.metadata = metadata;
        this.resource = resource;
    }
}
