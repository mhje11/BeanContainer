package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



@Controller
@RequiredArgsConstructor
@Slf4j
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
        return "member/admin";
    }

    //로그인 페이지
    @GetMapping("/login")
    public String showLoginForm() {
        return "member/loginForm";
    }


    //회원가입 페이지
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("memberSignUpDto");
        return "member/signupForm";
    }

    //마이페이지
    @GetMapping("/mypage/{userId}")
    public String showMyPage(@PathVariable String userId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String authenticatedUserId = (String) authentication.getPrincipal();

            if (authenticatedUserId.equals(userId)) {
                model.addAttribute("userId", userId);
                model.addAttribute("authorities", authentication.getAuthorities());
                return "member/myPage";
            }
        }
        return "redirect:/login"; // 인증되지 않은 사용자는 로그인 페이지로 리다이렉트
    }

}

