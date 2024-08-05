package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.SignUpDto;
import com.beancontainer.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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



    @PostMapping("/signup")
    public String signUp(@ModelAttribute SignUpDto signUpDto, RedirectAttributes redirectAttributes) {
        try {
            memberService.signUp(signUpDto);
            redirectAttributes.addFlashAttribute("signupSuccess", true);
            return "redirect:/login";
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/signup";
        }
    }




    //회원가입 페이지
    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("memberSignUpDto", new SignUpDto());
        return "signupForm";
    }


    //마이페이지
    @GetMapping("/mypage/{userId}")
    public String showMyPage(@PathVariable Long userId, Model model) {


        return "myPage";
    }

}
