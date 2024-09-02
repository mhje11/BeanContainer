package com.beancontainer.global.auth.controller;


import com.beancontainer.global.auth.service.RevokeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//소셜 회원 탈퇴 Controller
@RestController
@RequestMapping("/api/oauth2/revoke")
public class RevokeController {
    private final RevokeService revokeService;

    public RevokeController(RevokeService revokeService) {
        this.revokeService = revokeService;
    }

    @DeleteMapping("/naver")
    public
}
