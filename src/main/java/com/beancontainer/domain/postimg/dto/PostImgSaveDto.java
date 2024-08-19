package com.beancontainer.domain.postimg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class PostImgSaveDto {
    private Long postId;
    private MultipartFile img;

    public PostImgSaveDto(MultipartFile image) {
        this.img = image;
    }
}
