package com.rejs.flashnote.domain.image.controller;

import com.rejs.flashnote.domain.image.dto.ImageWithMetadata;
import com.rejs.flashnote.domain.image.service.ImageService;
import com.rejs.flashnote.global.security.utils.PrincipalUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Long>> uploadImage(
            @RequestPart("file") MultipartFile file
    ) {
        Long memberId = PrincipalUtils.getMemberId();
        Long id = imageService.uploadImageReturnId(file, memberId);
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) {
        ImageWithMetadata image = imageService.getImageResourceWithMetadata(id);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(image.getMetadata().getContentType()))
                .contentLength(image.getMetadata().getSize())
                .body(image.getResource());
    }

}
