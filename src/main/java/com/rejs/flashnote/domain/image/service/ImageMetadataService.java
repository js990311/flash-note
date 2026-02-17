package com.rejs.flashnote.domain.image.service;

import com.rejs.flashnote.domain.image.entity.ImageMetadata;
import com.rejs.flashnote.domain.image.repository.ImageMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ImageMetadataService {
    private final ImageMetadataRepository imageMetadataRepository;

    // Create
    @Transactional
    public Long create(Long tsid, String s3Key, String originalFilename, MultipartFile file, Long memberId){
        ImageMetadata metadata = ImageMetadata.builder()
                .id(tsid)
                .s3Key(s3Key)
                .fileName(originalFilename)
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .memberId(memberId)
                .build();
        return imageMetadataRepository.save(metadata).getId();
    }

    @Transactional
    public Long update(Long id){
        ImageMetadata imageMetadata = imageMetadataRepository.findById(id).orElseThrow();
        imageMetadata.uploadSuccess();
        return imageMetadata.getId();
    }
}
