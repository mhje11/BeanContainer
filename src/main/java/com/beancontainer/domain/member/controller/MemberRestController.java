package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.LoginRequestDTO;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.memberprofileimg.service.ProfileImageService;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MemberRestController {
    private final MemberService memberService;
    private final ProfileImageService profileImageService;


    @PostMapping("/mypage/{userId}/updateNickname")
    public ResponseEntity<Map<String, String>> updateNickname(@PathVariable String userId,
                                                              @RequestBody Map<String, String> payload) {
        String newNickname = payload.get("newNickname");

        try {
            memberService.updateNickname(userId, newNickname);
            Map<String, String> response = new HashMap<>();
            response.put("message", "닉네임 변경 완료");
            response.put("newNickname", newNickname);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("닉네임 변경 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "닉네임 변경 중 오류가 발생했습니다."));
        }
    }

    @PostMapping("/mypage/{userId}/uploadProfileImage")
    public ResponseEntity<Map<String, String>> uploadProfileImage(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) {

        try {
            String imageUrl = profileImageService.updateProfileImage(userId, file);
            return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "이미지 업로드에 실패했습니다."));
        }
    }

    @PostMapping("/mypage/{userId}/deleteProfileImage")
    public ResponseEntity<Map<String, String>> deleteProfileImage(Principal principal) {
        String userId = principal.getName();
        profileImageService.deleteExistingProfileImage(userId);
        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 제거되었습니다."));
    }


    @PostMapping("/mypage/{userId}/deleteAccount")
    public ResponseEntity<String> deleteAccount(@PathVariable String userId) {
        memberService.deleteAccount(userId);
        return ResponseEntity.ok().body("계정이 삭제되었습니다.");
    }
}
