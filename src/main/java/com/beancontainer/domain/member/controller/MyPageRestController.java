package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.member.profileimage.service.ProfileImageService;
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
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MyPageRestController {
    private final MemberService memberService;
    private final ProfileImageService profileImageService;


    @PostMapping("/mypage/{userId}/updateNickname")
    public ResponseEntity<Map<String, String>> updateNickname(@PathVariable String userId,
                                                              @RequestBody Map<String, String> payload) {
        String newNickname = payload.get("newNickname");
        memberService.updateNickname(userId, newNickname);
        return ResponseEntity.ok(Map.of("message", "닉네임 변경 완료", "newNickname", newNickname));
    }

    @PostMapping("/mypage/{userId}/uploadProfileImage")
    public ResponseEntity<Map<String, String>> uploadProfileImage( @PathVariable String userId, @RequestParam("file") MultipartFile file) throws IOException {

        String imageUrl = profileImageService.updateProfileImage(userId, file);
        return ResponseEntity.ok(Map.of("imageUrl", imageUrl));
    }

    @PostMapping("/mypage/{userId}/deleteProfileImage")
    public ResponseEntity<Map<String, String>> deleteProfileImage(Principal principal) {
        String userId = principal.getName();
        profileImageService.deleteExistingProfileImage(userId);
        return ResponseEntity.ok(Map.of("message", "프로필 이미지가 제거되었습니다."));
    }

    @PostMapping("/mypage/{userId}/deleteAccount")
    public ResponseEntity<String> deleteAccount(@PathVariable String userId) {
        memberService.cancelAccount(userId);
        return ResponseEntity.ok().body("계정이 탈퇴 되었습니다.");
    }

}
