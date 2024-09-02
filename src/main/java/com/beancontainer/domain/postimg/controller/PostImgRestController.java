package com.beancontainer.domain.postimg.controller;

import com.beancontainer.domain.postimg.dto.PostImgResponseDto;
import com.beancontainer.domain.postimg.service.PostImgService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/postimages")
@RequiredArgsConstructor
@Slf4j
public class PostImgRestController {
    private final PostImgService postImgService;

    @PostMapping
    public ResponseEntity<PostImgResponseDto> uploadImage(@RequestParam("upload") MultipartFile image) throws IOException {
        PostImgResponseDto response = postImgService.saveImage(image);
        return ResponseEntity.ok(response);

    }
}
