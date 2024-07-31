package com.beancontainer.domain.member.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class UserController {
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
    @GetMapping("/loginform")
    public String showLoginForm() {
        return "loginForm";
    }

    //회원가입 페이지
    @GetMapping("/signupform")
    public String showSignUpForm() {
        return "signUpForm";
    }

    //마이페이지
    @GetMapping("/mypage/{userId}")
    public String showMyPage(@PathVariable Long userId, Model model) {


        return "myPage";
    }

}
