package com.beancontainer.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
//    private Long id;
//    private Member member;
    private String username;
    private String title;
    private String content;
    private String uuid;
}
