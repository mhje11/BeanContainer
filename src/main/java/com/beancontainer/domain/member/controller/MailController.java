package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.service.MailService;
import com.beancontainer.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class MailController {
    private final MemberService memberService;
    private final MailService mailService;
    @PostMapping("/auth/signup/email-validate")
    public ResponseEntity<String> mailConfirm(@RequestParam(value = "eamil", required = false) String email) throws Exception {
        String code = mailService.sendSimpleMessage(email);
        log.info("인증코드 : " + code);
        return ResponseEntity.ok(code);

    }
}
