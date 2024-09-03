package com.beancontainer.domain.member.profileimage.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.profileimage.repository.ProfileImageRepository;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final S3Client s3Client;
    private final ProfileImageRepository profileImageRepository;
    private final MemberRepository memberRepository;

    private static final String DEFAULT_PROFILE_IMAGE = "/images/BeanContainer.png";

    public String updateProfileImage(String userId, MultipartFile image) throws IOException {
        String originalName = image.getOriginalFilename();
        String name = getFileName(originalName);

        //s3 에 업로드
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(name)
                        .build(),
                RequestBody.fromInputStream(image.getInputStream(), image.getSize())
        );
        String imageUrl = generateImageUrl(name);

        // 기존 프로필 이미지 삭제
        deleteExistingProfileImage(userId);

        // 새로운 프로필 이미지 URL 저장
        Member member = memberRepository.findByUserId(userId).orElseThrow();
        member.updateProfileImageUrl(imageUrl);
        memberRepository.save(member);

        return imageUrl;
    }

    //기존 프로필 이미지 삭제
    @Transactional
    public void deleteExistingProfileImage(String userId) {
        Member member = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        if (member.getProfileImageUrl() != null && !member.getProfileImageUrl().equals(DEFAULT_PROFILE_IMAGE)) {
            String fileName = member.getProfileImageUrl().substring(member.getProfileImageUrl().lastIndexOf("/") + 1);
            deleteImageFromS3(fileName);

            // DB에서도 삭제
            profileImageRepository.deleteByFileName(fileName);

            // profileImageUrl을 null로 설정
            member.updateProfileImageUrl(null);
            memberRepository.save(member);
        }
    }


    //s3에 업로드 된 이미지 제거
    private void deleteImageFromS3(String fileName) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build()
        );
    }

    //이미지 확장자
    public String extractExtension(String originalName) {
        int index = originalName.lastIndexOf(".");
        return originalName.substring(index + 1);
    }

    //파일 이름 UUID 변환
    private String getFileName(String originalName) {
        return UUID.randomUUID() + "." + extractExtension(originalName);
    }


    private String generateImageUrl(String fileName) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }
}