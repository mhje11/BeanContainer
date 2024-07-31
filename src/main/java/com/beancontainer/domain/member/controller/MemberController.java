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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
    @GetMapping("/loginform")
    public String showLoginForm() {
        return "loginForm";
    }

    //회원가입 페이지
    @GetMapping("/signupform")
    public String showSignUpForm(Model model) {
        model.addAttribute("memberSignUpDto", new MemberSignUpDto());
        return "signUpForm";
    }

    @PostMapping("/signupform")
    public String signUp(@Valid @ModelAttribute MemberSignUpDto signUpDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "signUpForm";
        } //유효성 검사 오류가 있으면 회원가입 폼으로

        try {
            Member newMember = memberService.signUp(signUpDto);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 회원 ID: " + newMember.getId());
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        } catch (IllegalStateException e) {
            bindingResult.rejectValue("userId", "error.userId", e.getMessage());
            return "signUpForm"; //중복시 에러 출력 후 회원가입 폼으로
        } catch (Exception e) {
            bindingResult.reject("error.signUp", "회원가입 중 오류가 발생했습니다.");
            return "signUpForm"; //기타 예외 발생 시 에러 처리
        }
    }

    //마이페이지
    @GetMapping("/mypage/{userId}")
    public String showMyPage(@PathVariable Long userId, Model model) {


        return "myPage";
    }

}
