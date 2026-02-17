package com.rejs.flashnote.global.image.repository;

import com.rejs.flashnote.global.image.S3Properties;
import com.rejs.flashnote.global.image.exception.ImageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalFileRepositoryTest {
    private LocalFileRepository localFileRepository;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        // 1. S3Properties 설정 (Type-safe Configuration)
        S3Properties s3Properties = new S3Properties();
        s3Properties.setTempDir(tempDir.toString());

        // 2. Repository 생성 및 초기화
        localFileRepository = new LocalFileRepository(s3Properties);
        localFileRepository.init();
    }

    @Test
    @DisplayName("파일 저장 성공 테스트")
    void saveTest() throws IOException {
        // Given
        String filename = "test-image.png";
        String content = "fake-image-content";
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "original.png",
                "image/png",
                content.getBytes(StandardCharsets.UTF_8)
        );

        // When
        Path savedPath = localFileRepository.save(multipartFile, filename);

        // Then

        // 1. 경로가 null이 아닌지 검증
        assertNotNull(savedPath, "저장된 경로는 null이 아니어야 합니다.");

        // 2. 파일이 실제로 존재하는지 검증
        assertTrue(Files.exists(savedPath), "저장된 경로에 파일이 실제로 존재해야 합니다.");

        // 3. 파일 내용이 일치하는지 검증
        String savedContent = Files.readString(savedPath);
        assertEquals(content, savedContent, "파일의 내용이 원본과 일치해야 합니다.");
    }

    @Test
    @DisplayName("파일 삭제 성공 테스트")
    void deleteTest() throws IOException {
        // Given
        Path dummyFile = tempDir.resolve("delete-target.tmp");
        Files.writeString(dummyFile, "to be deleted");

        // 삭제 전 존재 확인
        assertTrue(Files.exists(dummyFile));

        // When
        localFileRepository.delete(dummyFile);

        // Then
        assertFalse(Files.exists(dummyFile), "삭제 후에는 파일이 존재하지 않아야 합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 파일을 삭제해도 예외가 발생하지 않아야 한다")
    void deleteNonExistentFileTest() {
        // Given
        Path nonExistentPath = tempDir.resolve("ghost.tmp");

        // When & Then
        assertDoesNotThrow(() -> localFileRepository.delete(nonExistentPath));
    }

    @Test
    @DisplayName("초기화 시 경로가 이상하면 예외가 발생해야 한다")
    void initExceptionTest() {
        // Given
        S3Properties badProperties = new S3Properties();
        badProperties.setTempDir("\0");

        LocalFileRepository badRepo = new LocalFileRepository(badProperties);

        // When & Then
        // ImageException이 발생하는지 검증
        assertThrows(ImageException.class, () -> badRepo.init());
    }
}