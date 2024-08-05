package com.beancontainer.domain.postimg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PostImgRequestDto {
    private UUID name;
    private UUID path;
    private String type;
}
