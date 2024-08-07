package com.beancontainer.domain.postimg.service;

import com.beancontainer.domain.postimg.entity.PostImg;
import com.beancontainer.domain.postimg.repository.PostImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostImgService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketname;
    private final S3Client s3Client;

    private final PostImgRepository postImgRepository;

    @Transactional
    public String saveImage(MultipartFile image) throws IOException {
        String originalName = image.getOriginalFilename();  // 본래 이미지 이름
        String name = getFileName(originalName);    // uuid로 변환된 이름

        // S3에 이미지 업로드
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketname)
                        .key(name)
                        .build(),
                RequestBody.fromInputStream(image.getInputStream(), image.getSize())    // 파일의 본체
        );

        return generateImageUrl(name);
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

    private String generateImageUrl(String name) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketname, name);
        // https://{bucket-name}.s3.{region}.amazonaws.com/{object-key}
    }

    @Transactional
    public void save(PostImg postImg) {
        postImgRepository.save(postImg);
    }

}
