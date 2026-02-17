package com.rejs.flashnote.domain.image.service;

import com.rejs.flashnote.domain.image.dto.S3ViewMetadata;
import com.rejs.flashnote.domain.image.entity.ImageMetadata;
import com.rejs.flashnote.domain.image.repository.ImageMetadataRepository;
import com.rejs.flashnote.global.image.S3Properties;
import com.rejs.flashnote.global.image.exception.ImageException;
import com.rejs.flashnote.global.image.repository.LocalFileRepository;
import com.rejs.flashnote.global.image.repository.S3ImageRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {
    private final S3ImageRepository s3ImageRepository;
    private final LocalFileRepository localFileRepository;
    private final ImageMetadataService imageMetadataService;
    private final S3Properties s3Properties;

    public CompletableFuture<Long> uploadImage(MultipartFile file, Long memberId) {
        // 1. 파일 정보 준비
        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);
        Long tsid = TSID.fast().toLong();
        String tempFilename = tsid + "." + extension;
        String s3Key = "images/" + tempFilename;

        // 2. 로컬 임시 저장
        Path tempPath = localFileRepository.save(file, tempFilename);
        imageMetadataService.create(tsid, s3Key, originalFilename, file, memberId);

        try {
            FileInputStream fis = new FileInputStream(tempPath.toFile());

            // 3. S3 비동기 업로드
            return s3ImageRepository.putImageAsync(
                    s3Properties.getBucket(),
                    s3Key,
                    fis,
                    file.getSize(),
                    file.getContentType()
            ).handle((response, throwable) -> {
                // 4. 후처리 (파일 스트림 닫기 및 임시 파일 삭제)
                closeStream(fis);
                localFileRepository.delete(tempPath);

                if (throwable != null) {
                    throw new ImageException("S3 업로드 실패", throwable);
                }

                // 5. DB 메타데이터 저장 (Entity의 @Tsid가 ID를 자동 생성)
                return imageMetadataService.update(tsid);
            });
        } catch (IOException e) {
            localFileRepository.delete(tempPath);
            throw new ImageException("파일 처리 중 오류 발생", e);
        }
    }

    public Resource getImageResource(Long id) {
        // 1. DB에서 메타데이터 조회 (S3 Key 확인)
        S3ViewMetadata metadata = imageMetadataService.read(id);

        // 2. S3에서 스트림 데이터 조회
        InputStreamResource imageAsResource = s3ImageRepository.getImageAsResource(s3Properties.getBucket(), metadata.getS3Key());
        return imageAsResource;
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "png";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void closeStream(FileInputStream fis) {
        try {
            if (fis != null) fis.close();
        } catch (IOException e) {
            log.warn("Stream close fail", e);
        }
    }
}
