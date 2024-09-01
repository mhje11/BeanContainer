package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.member.profileimage.service.ProfileImageService;
import com.beancontainer.global.auth.service.CookieService;
import com.beancontainer.global.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@Slf4j
public class MyPageRestController {
    private final MemberService memberService;
    private final ProfileImageService profileImageService;
    private final CookieService cookieService;
    private final RefreshTokenService refreshTokenService;


    @PostMapping("/{userId}/nickname")
    public ResponseEntity<Map<String, String>> updateNickname(@PathVariable String userId,
                                                              @RequestBody Map<String, String> payload, Principal principal) {
        // 현재 로그인한 사용자와 userId가 일치하는지 확인
        if (!userId.equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String newNickname = payload.get("newNickname");
        memberService.updateNickname(userId, newNickname);
        return ResponseEntity.ok(Map.of("message", "닉네임 변경 완료", "newNickname", newNickname));
    }

    @PostMapping("/{userId}/profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage( @PathVariable String userId, @RequestParam("file") MultipartFile file, Principal principal) throws IOException {

        // 현재 로그인한 사용자와 userId가 일치하는지 확인
        if (!userId.equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String imageUrl = profileImageService.updateProfileImage(userId, file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @DeleteMapping("/{userId}/profile-image")
    public ResponseEntity<Map<String, String>> deleteProfileImage(@PathVariable String userId,
                                                                  Principal principal) {
        // 현재 로그인한 사용자와 userId가 일치하는지 확인
        if (!userId.equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        profileImageService.deleteExistingProfileImage(userId);
        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 제거되었습니다."));
    }

    @DeleteMapping("/{userId}/account")
    public ResponseEntity<String> deleteAccount(@PathVariable String userId,
                                                Principal principal, HttpServletResponse response) {
        // 현재 로그인한 사용자와 userId가 일치하는지 확인
        if (!userId.equals(principal.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        memberService.cancelAccount(userId);
        // 관련된 모든 쿠키 삭제
        cookieService.deleteCookie(response, "accessToken");
        cookieService.deleteCookie(response, "refreshToken");

        return ResponseEntity.ok().body("계정이 탈퇴 되었습니다.");


    }

}
