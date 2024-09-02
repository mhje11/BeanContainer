package com.beancontainer.domain.postimg.service;

import com.beancontainer.domain.postimg.dto.PostImgResponseDto;
import com.beancontainer.domain.postimg.entity.PostImg;
import com.beancontainer.domain.postimg.repository.PostImgRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostImgService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketname;
    private final S3Client s3Client;
    private final List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");

    private final PostImgRepository postImgRepository;

    @Transactional
    public PostImgResponseDto saveImage(MultipartFile image) throws IOException {

        checkImageFormat(image);    // 이미지 파일 형식 검사

        String originalName = image.getOriginalFilename();  // 본래 이미지 이름
        String name = getFileName(originalName);    // uuid로 변환된 이름

        // S3에 이미지 업로드
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketname)
                        .key(name)
                        .contentType(extractExtension(originalName))
                        .build(),
                RequestBody.fromInputStream(image.getInputStream(), image.getSize())    // 파일의 본체
        );

        String imageUrl = generateImageUrl(name);   // S3 url

        return new PostImgResponseDto(originalName, name, imageUrl);
    }

    // 이미지 판별
    public void checkImageFormat(MultipartFile image) {
        String extension = extractExtension(image.getOriginalFilename());
        if(!allowedExtensions.contains(extension)) {
            throw new CustomException(ExceptionCode.INVALID_IMAGE_FORMAT);
        }
    }

    // 이미지 확장자 추출
    public String extractExtension(String originalName) {
        int index = originalName.lastIndexOf(".");
        return originalName.substring(index + 1);
    }

    // 이미지 파일 이름 UUID로 변환
    public String getFileName(String originalName) {
        return UUID.randomUUID() + "." + extractExtension(originalName);
    }

    // S3 이미지 url
    private String generateImageUrl(String name) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketname, name);
        // https://{bucket-name}.s3.{region}.amazonaws.com/{object-key}
    }

    @Transactional
    public void save(PostImg postImg) {
        postImgRepository.save(postImg);
    }

    public void deleteImage(String imageUrl) {
        String fileName = fileNameFromUrl(imageUrl);
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucketname)
                        .key(fileName)
                        .build()
        );
    }

    private String fileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}
