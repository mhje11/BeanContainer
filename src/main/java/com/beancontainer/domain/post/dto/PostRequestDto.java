package com.beancontainer.domain.post.dto;

import com.beancontainer.domain.postimg.dto.PostImgSaveDto;
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
    private List<PostImgSaveDto> images = new ArrayList<>();

    public List<PostImgSaveDto> getImages() {
        return images != null ? images : new ArrayList<>();
    }
}