package com.rejs.flashnote.global.image.repository;

import com.rejs.flashnote.global.image.S3Properties;
import com.rejs.flashnote.global.image.exception.ImageException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Slf4j
@Component
public class LocalFileRepository {
    private final S3Properties s3Properties;

    private Path rootPath;

    @PostConstruct
    public void init() {
        try {
            this.rootPath = Paths.get(s3Properties.getTempDir());
            if (!Files.exists(this.rootPath)) {
                Files.createDirectories(this.rootPath);
            }
        } catch (Exception e) {
            throw new ImageException("로컬 임시 디렉토리 초기화 실패", e);
        }
    }

    public Path save(MultipartFile file, String filename) {
        try {
            Path targetPath = this.rootPath.resolve(filename);
            file.transferTo(targetPath);
            return targetPath;
        } catch (IOException e) {
            throw new ImageException("로컬 파일 저장 실패: " + filename, e);
        }
    }

    public void delete(Path path) {
        try {
            if (path != null) {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.warn("로컬 임시 파일 삭제 실패: {}", path, e);
        }
    }
}
