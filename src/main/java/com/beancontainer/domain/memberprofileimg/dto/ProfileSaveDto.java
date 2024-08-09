package com.beancontainer.domain.memberprofileimg.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class ProfileSaveDto {
    private Long profileId;
    private MultipartFile image;

    public ProfileSaveDto(MultipartFile image) {
        this.image = image;
    }
}
