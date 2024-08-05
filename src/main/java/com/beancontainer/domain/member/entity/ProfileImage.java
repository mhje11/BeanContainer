package com.beancontainer.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Entity
@Table(name = "profile_images")
@Getter
@NoArgsConstructor
public class ProfileImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID fileName; //파일명 UUID 지정

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID filePath; //파일경로도 UUID 지정

    private String fileType;

    public ProfileImage(UUID fileName, UUID filePath, String fileType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
    }

    //이미지 인스턴스 생성
    public static ProfileImage createProfileImage(UUID fileName, UUID filePath, String fileType) {
        return new ProfileImage(fileName, filePath, fileType);
    }
}
