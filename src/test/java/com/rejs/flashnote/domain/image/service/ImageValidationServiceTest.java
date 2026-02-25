package com.rejs.flashnote.domain.image.service;

import com.rejs.flashnote.global.image.exception.ImageErrorCode;
import com.rejs.flashnote.global.image.exception.ImageException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageValidationServiceTest {

    private final ImageValidationService imageValidationService = new ImageValidationService("1MB");

    @Test
    void validate_png_success() {
        byte[] png = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x00
        };

        MockMultipartFile file = new MockMultipartFile("file", "../t e st.png", "image/png", png);

        ImageValidationService.ValidatedImage validated = imageValidationService.validate(file);

        assertThat(validated.contentType()).isEqualTo("image/png");
        assertThat(validated.extension()).isEqualTo("png");
    }

    @Test
    void validate_fail_when_mime_not_allowed() {
        byte[] gifHeader = new byte[]{'G', 'I', 'F', '8', '9', 'a'};
        MockMultipartFile file = new MockMultipartFile("file", "sample.gif", "image/gif", gifHeader);

        assertThatThrownBy(() -> imageValidationService.validate(file))
                .isInstanceOf(ImageException.class)
                .extracting(ex -> ((ImageException) ex).getErrorCode())
                .isEqualTo(ImageErrorCode.UNSUPPORTED_MIME_TYPE);
    }

    @Test
    void validate_fail_when_signature_mismatch() {
        byte[] png = new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47,
                0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x00
        };
        MockMultipartFile file = new MockMultipartFile("file", "sample.jpg", "image/jpeg", png);

        assertThatThrownBy(() -> imageValidationService.validate(file))
                .isInstanceOf(ImageException.class)
                .extracting(ex -> ((ImageException) ex).getErrorCode())
                .isEqualTo(ImageErrorCode.INVALID_SIGNATURE);
    }

    @Test
    void validate_fail_when_file_too_large() {
        byte[] jpeg = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00};
        byte[] oversized = new byte[1_048_577];
        System.arraycopy(jpeg, 0, oversized, 0, jpeg.length);

        MockMultipartFile file = new MockMultipartFile("file", "sample.jpg", "image/jpeg", oversized);

        assertThatThrownBy(() -> imageValidationService.validate(file))
                .isInstanceOf(ImageException.class)
                .extracting(ex -> ((ImageException) ex).getErrorCode())
                .isEqualTo(ImageErrorCode.FILE_TOO_LARGE);
    }
}