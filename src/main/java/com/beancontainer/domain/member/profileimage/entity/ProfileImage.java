package com.beancontainer.domain.member.profileimage.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "profile_images")
@Getter
@NoArgsConstructor
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public void setFilePath(String path) {
        this.filePath = path;
    }
}
