package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.memberprofileimg.entity.ProfileImage;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.memberprofileimg.service.ProfileImageService;
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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MemberAPIController {
    private final MemberService memberService;
    private final ProfileImageService profileImageService;


    @PostMapping("/mypage/{userId}/updateNickname")
    public ResponseEntity<?> updateNickname(@PathVariable String userId, @RequestBody Map<String, String> payload, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String newNickname = payload.get("newNickname");
        if (newNickname == null || newNickname.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "새 닉네임을 입력해주세요"));
        }

        try {
            memberService.updateNickname(userId, newNickname);
            return ResponseEntity.ok()
                    .body(Map.of("success", true, "message", "닉네임 변경 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "닉네임 변경 중 오류가 발생했습니다." + e.getMessage()));
        }
    }

    @PostMapping("/mypage/{userId}/uploadProfileImage")
    public ResponseEntity<?> uploadProfileImage(@PathVariable String userId, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if(!customUserDetails.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            String imageUrl = profileImageService.updateProfileImage(userId, file);
            return ResponseEntity.ok().body(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드에 실패했습니다.");
        }
    }


    @PostMapping("/mypage/{userId}/deleteAccount")
    public ResponseEntity<?> deleteAccount(@PathVariable String userId) {
        memberService.deleteAccount(userId);
        return ResponseEntity.ok().body("계정이 삭제되었습니다.");
    }
}
