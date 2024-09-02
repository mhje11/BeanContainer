package com.beancontainer.global.auth.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.plaf.SpinnerUI;

//소셜 회원 탈퇴 service
@Service
public class RevokeService {
    private final MemberRepository memberRepository;

    public RevokeService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;


    @Transactional
    public void revokeNaver(String accessToken) {
        String naverUrl = "https://nid.naver.com/oauth2.0/token";
    }

    @Transactional
    public void revokeKakao(String accessToken) {
        String kakaoUrl = "https://kapi.kakao.com/v1/user/unlink";
    }

}
