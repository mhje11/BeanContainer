package com.beancontainer.domain.postimg.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostImgResponseDto {
    private String originName;  // 파일 본래 이름
    private String name;    // 저장되는 이름
    private String url; // s3에 업로드된 이미지 url
}
