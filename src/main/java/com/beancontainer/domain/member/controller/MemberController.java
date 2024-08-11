package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.memberprofileimg.service.ProfileImageService;
import com.beancontainer.global.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

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
    public String showMyPage(@PathVariable String userId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String authenticatedUserId = userDetails.getUserId();

        if (!authenticatedUserId.equals(userId)) {
            return "redirect:/"; // 또는 에러 페이지로 리다이렉트
        }

        // 여기서 필요한 사용자 정보를 가져와 모델에 추가
        Member member = memberService.findByUserId(userId);
        model.addAttribute("userId", userId);
        model.addAttribute("name", member.getName());
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("profileImageUrl", member.getProfileImageUrl());
        model.addAttribute("authorities", authentication.getAuthorities());

        return "member/myPage";
    }







}

