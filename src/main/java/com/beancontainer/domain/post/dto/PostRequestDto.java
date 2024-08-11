package com.beancontainer.domain.post.dto;

import com.beancontainer.domain.postimg.dto.PostImgSaveDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
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
