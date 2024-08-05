package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.LoginDto;
import com.beancontainer.domain.member.dto.SignUpDto;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberRestController {
    private final MemberService memberService;


}
