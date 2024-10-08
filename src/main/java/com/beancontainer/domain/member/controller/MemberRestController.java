package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.dto.VerifyCodeDTO;
import com.beancontainer.domain.member.service.AuthService;
import com.beancontainer.domain.member.service.MailService;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class MemberRestController {
    private final AuthService authService;
    private final MemberService memberService;
    private final MailService mailService;


    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(Collections.singletonMap("message", "회원가입이 성공적으로 완료되었습니다.")); //JSON 형태로 응답
    }

    //이메일 인증 코드 발송
    @PostMapping("/signup/email-send")
    public ResponseEntity<String> sendEmailCode(@RequestParam(value = "email", required = false) String email) {
        String authCode = mailService.sendSimpleMessage(email);
        log.info("인증 코드 : " + authCode);
        return ResponseEntity.ok("인증 코드가 전송 되었습니다.");
    }

    //이메일 인증 코드 확인
    @PostMapping("/signup/check-code")
    public ResponseEntity<String> verifyEmailCode(@RequestBody VerifyCodeDTO verifyCode) {
        mailService.verifyEmailCode(verifyCode.getCode());
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }


    //아이디 중복 체크
    @GetMapping("/signup/check-userid")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestParam String userId) {
        boolean exists = memberService.existsByUserId(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}

