package com.rejs.flashnote.domain.image.repository;

import com.rejs.flashnote.domain.image.entity.ImageMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Long> {
}
