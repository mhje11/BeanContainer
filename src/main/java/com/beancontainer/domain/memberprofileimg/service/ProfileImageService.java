package com.beancontainer.domain.memberprofileimg.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.memberprofileimg.entity.ProfileImage;
import com.beancontainer.domain.memberprofileimg.repository.ProfileImageRepository;
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

    private static final String DEFAULT_PROFILE_IMAGE = "images/BeanContainer.png";

    @Transactional
    public ProfileImage getOrCreateDefaultProfileImage(Member member) {
        if (member.getProfileImage() == null) {
            ProfileImage defaultImage = new ProfileImage(
                    DEFAULT_PROFILE_IMAGE,
                    generateImageUrl(DEFAULT_PROFILE_IMAGE),
                    "image/png"
            );
            profileImageRepository.save(defaultImage);
            member.setProfileImage(defaultImage);
            memberRepository.save(member);
            return defaultImage;
        }
        return member.getProfileImage();
    }

    public String getProfileImageUrl(Member member) {
        if (member.getProfileImage() != null && member.getProfileImage().getFilePath() != null) {
            return member.getProfileImage().getFilePath() + "?v=" + System.currentTimeMillis();
        }
        return DEFAULT_PROFILE_IMAGE;
    }

    @Transactional
    public ProfileImage updateProfileImage(String userId, MultipartFile file) throws IOException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String fileName = getFileName(file.getOriginalFilename());

        String imageUrl = uploadImageToS3(fileName, file);

        if (member.getProfileImage() != null && !member.getProfileImage().getFileName().equals(DEFAULT_PROFILE_IMAGE)) {
            deleteImageFromS3(member.getProfileImage().getFileName());
        }

        ProfileImage profileImage = new ProfileImage(
                fileName,
                imageUrl,
                file.getContentType()
        );
        profileImageRepository.save(profileImage);

        member.setProfileImage(profileImage);
        memberRepository.save(member);

        return profileImage;
    }

    private String uploadImageToS3(String fileName, MultipartFile file) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
        );
        return generateImageUrl(fileName);
    }

    private void deleteImageFromS3(String fileName) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build()
        );
    }

    private String getFileName(String originalName) {
        return UUID.randomUUID() + "-" + originalName;
    }

    private String generateImageUrl(String fileName) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }
}