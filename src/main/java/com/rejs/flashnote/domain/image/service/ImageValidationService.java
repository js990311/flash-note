package com.rejs.flashnote.domain.image.service;

import com.rejs.flashnote.global.image.exception.ImageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

@Component
public class ImageValidationService {
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/png", "image/jpeg", "image/webp");
    private final long maxFileSizeBytes;

    public ImageValidationService(@Value("${spring.servlet.multipart.max-file-size:10MB}") String maxFileSize) {
        this.maxFileSizeBytes = org.springframework.util.unit.DataSize.parse(maxFileSize).toBytes();
    }

    public ValidatedImage validate(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            throw ImageException.emptyFile();
        }

        String declaredMime = normalize(file.getContentType());
        if (!ALLOWED_MIME_TYPES.contains(declaredMime)) {
            throw ImageException.unsupportedMimeType(file.getContentType());
        }

        if (file.getSize() > maxFileSizeBytes) {
            throw ImageException.fileTooLarge(file.getSize(), maxFileSizeBytes);
        }

        byte[] header = readHeader(file);
        String sniffedMime = sniffMimeType(header);

        if (!ALLOWED_MIME_TYPES.contains(sniffedMime) || !declaredMime.equals(sniffedMime)) {
            throw ImageException.invalidSignature();
        }

        String extension = extensionByMime(sniffedMime);

        return new ValidatedImage(file.getOriginalFilename(), sniffedMime, extension, file.getSize());
    }

    private byte[] readHeader(MultipartFile file) {
        try {
            return file.getInputStream().readNBytes(12);
        } catch (IOException e) {
            throw ImageException.fileProcessingFailed(e);
        }
    }

    private String sniffMimeType(byte[] header) {
        if (isPng(header)) {
            return "image/png";
        }
        if (isJpeg(header)) {
            return "image/jpeg";
        }
        if (isWebp(header)) {
            return "image/webp";
        }
        throw ImageException.invalidSignature();
    }

    private boolean isPng(byte[] header) {
        return header.length >= 8
                && header[0] == (byte) 0x89
                && header[1] == 0x50
                && header[2] == 0x4E
                && header[3] == 0x47
                && header[4] == 0x0D
                && header[5] == 0x0A
                && header[6] == 0x1A
                && header[7] == 0x0A;
    }

    private boolean isJpeg(byte[] header) {
        return header.length >= 3
                && header[0] == (byte) 0xFF
                && header[1] == (byte) 0xD8
                && header[2] == (byte) 0xFF;
    }

    private boolean isWebp(byte[] header) {
        if (header.length < 12) {
            return false;
        }

        String riff = new String(header, 0, 4, StandardCharsets.US_ASCII);
        String webp = new String(header, 8, 4, StandardCharsets.US_ASCII);

        return "RIFF".equals(riff) && "WEBP".equals(webp);
    }

    private String extensionByMime(String mimeType) {
        return switch (mimeType) {
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/webp" -> "webp";
            default -> throw ImageException.unsupportedMimeType(mimeType);
        };
    }

    private String normalize(String contentType) {
        return contentType == null ? "" : contentType.toLowerCase(Locale.ROOT).trim();
    }

    public record ValidatedImage(String filename, String contentType, String extension, long size) {
    }
}
