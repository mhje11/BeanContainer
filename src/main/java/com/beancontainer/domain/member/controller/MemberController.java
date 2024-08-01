package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.MemberSignUpDto;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    //메인 페이지
    @GetMapping("/")
    public String showIndexPage() {
        return "index";
    }

    //관리자 페이지
    @GetMapping("/admin")
    public String showAdminPage() {
        return "admin";
    }

    //로그인 페이지
    @GetMapping("/login")
    public String showLoginForm() {
        return "loginForm";
    }


    //회원가입 페이지
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("memberSignUpDto", new MemberSignUpDto());
        return "signupForm";
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> signUp(@Valid @RequestBody MemberSignUpDto signUpDto) {
        try {
            Member newMember = memberService.signUp(signUpDto);
            return ResponseEntity.ok().body(Map.of("message", "회원가입이 완료되었습니다. 회원 ID: " + newMember.getId(),
                    "redirectUrl", "/login"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("회원가입 중 오류가 발생했습니다.");
        }
    }

    //마이페이지
    @GetMapping("/mypage/{userId}")
    public String showMyPage(@PathVariable Long userId, Model model) {


        return "myPage";
    }

}
