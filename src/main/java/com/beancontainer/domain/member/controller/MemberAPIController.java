package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.memberimg.entity.ProfileImage;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.memberimg.service.ProfileImageService;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MemberAPIController {
    private final MemberService memberService;
    private final ProfileImageService profileImageService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/mypage/{userId}/updateNickname")
    public ResponseEntity<?> updateNickname(@RequestParam String userId, @RequestParam String newNickname, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(!customUserDetails.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        memberService.updateNickname(userId, newNickname);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mypage/{userId}/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(@RequestParam String userId, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {
        if (!customUserDetails.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        ProfileImage profileImage = profileImageService.updateProfileImage(userId, file);
        return ResponseEntity.ok().body("/api/profileImage/" + profileImage.getFileName());
    }

    @GetMapping("/profileImage/{fileName}")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable String fileName) throws IOException {
        byte[] imageBytes = profileImageService.getProfileImageBytes(fileName);
        return ResponseEntity.ok().body(imageBytes);
    }

    @PostMapping("/mypage/deleteAccount")
    public ResponseEntity<?> deleteAccount(@RequestParam String userId) {
        memberService.deleteAccount(userId);
        return ResponseEntity.ok().build();
    }
}
