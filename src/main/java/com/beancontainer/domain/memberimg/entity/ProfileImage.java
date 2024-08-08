package com.beancontainer.domain.memberimg.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_images")
@Getter
@NoArgsConstructor
public class ProfileImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName; //파일명 UUID 지정

    @Column(nullable = false)
    private String filePath; //파일경로도 UUID 지정

    private String fileType;

    public ProfileImage(String fileName, String filePath, String fileType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    //이미지 인스턴스 생성
    public static ProfileImage createProfileImage(String fileName, String filePath, String fileType) {
        return new ProfileImage(fileName, filePath, fileType);
    }
}
