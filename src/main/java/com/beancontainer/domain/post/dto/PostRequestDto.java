package com.beancontainer.domain.post.dto;

import com.beancontainer.domain.postimg.dto.PostImgResponseDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String title;
    private String content;
    private List<PostImgResponseDto> imageInfos = new ArrayList<>();
    private List<String> unusedImageUrls = new ArrayList<>();
    private List<Long> deleteImages = new ArrayList<>();
}