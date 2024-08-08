package com.beancontainer.domain.memberimg.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.memberimg.entity.ProfileImage;
import com.beancontainer.domain.memberimg.repository.ProfileImageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ProfileImageService {
    private ProfileImageRepository profileImageRepository;
    private MemberRepository memberRepository;
    private MemberService memberService;


    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional
    public ProfileImage updateProfileImage(String userId, MultipartFile file) throws IOException {
        Member member = memberService.findByUserId(userId);
        String fileName = UUID.randomUUID().toString();
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fullFileName = fileName + fileExtension;
        String filePath = Paths.get(uploadDir, fullFileName).toString();

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path destinationFile = uploadPath.resolve(fullFileName);
        Files.write(destinationFile, file.getBytes());

        ProfileImage profileImage = ProfileImage.createProfileImage(fullFileName, filePath, file.getContentType());
        profileImageRepository.save(profileImage);

        member.setProfileImage(profileImage);
        memberRepository.save(member);

        return profileImage;
    }

    public byte[] getProfileImageBytes(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir, fileName);
        return Files.readAllBytes(filePath);
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
