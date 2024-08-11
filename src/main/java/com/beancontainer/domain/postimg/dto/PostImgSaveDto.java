package com.beancontainer.domain.postimg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class PostImgSaveDto {
    private Long postId;
    private MultipartFile img;

    public PostImgSaveDto(MultipartFile image) {
        this.img = image;
    }
}
