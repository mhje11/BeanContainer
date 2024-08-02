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

    @OneToOne(fetch = FetchType.LAZY) // 프로필 이미지는 1개만 업로드 가능
    @JoinColumn(name = "member_id")
    private Member member;

    public ProfileImage(UUID fileName, UUID filePath, String fileType, Member member) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.member = member;
    }

    //이미지 인스턴스 생성
    public static ProfileImage createProfileImage(UUID fileName, UUID filePath, String fileType, Member member) {
        return new ProfileImage(fileName, filePath, fileType, member);
    }
}
