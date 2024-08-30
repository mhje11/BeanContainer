package com.beancontainer.global.auth.service;


import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.auth.oauth2.dto.*;
import com.beancontainer.global.oauth2.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    public CustomOAuth2UserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 로그인 : " + oAuth2User);

        // 어떤 소셜 요청 로그인인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        try {
            switch (registrationId) {
                case "naver":
                    log.info("naver 로그인");
                    oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
                    break;

                case "kakao":
                    log.info("kakao 로그인");
                    oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
                    break;

                default:
                    log.error("로그인 실패: 지원하지 않는 로그인 제공자입니다. 등록 ID: {}", registrationId);
                    throw new IllegalArgumentException("지원하지 않는 로그인 제공자입니다.");
            }

            //서버에서 발급 받은 정보를 통해 사용자를 특정할 수 있는 id 생성
            String userId = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
            Optional<Member> memberOptional = memberRepository.findByUserId(userId);

            Member member;
            if (memberOptional.isPresent()) {
                member = memberOptional.get();
                log.info("소셜 유저 로그인 : {}", userId);
                member.updateOAuth2Info(oAuth2Response.getName(), oAuth2Response.getEmail());
                member = memberRepository.save(member);
            } else {
                // DB에 새 유저 저장
                member = Member.builder()
                        .userId(userId)
                        .email(oAuth2Response.getEmail())
                        .name(oAuth2Response.getName())
                        .nickname(oAuth2Response.getName()) // 이름을 닉네임과 동일하게 사용
                        .password("")
                        .provider(oAuth2Response.getProvider())
                        .providerId(oAuth2Response.getProviderId())
                        .role(Role.MEMBER)
                        .build();
                log.info("새로운 소셜 유저 생성");
                log.info("이름: {}", oAuth2Response.getName());
                log.info("이메일: {}", oAuth2Response.getEmail());
                log.info("Provider: {}", oAuth2Response.getProvider());
                log.info("ProviderId: {}", oAuth2Response.getProviderId());
                log.info("Attributes: {}", oAuth2Response.getAttributes());

                member = memberRepository.save(member);
            }

                OAuth2LoginDTO oAuth2LoginDTO = new OAuth2LoginDTO();
                oAuth2LoginDTO.setUserId(member.getUserId());
                oAuth2LoginDTO.setName(member.getName());
                oAuth2LoginDTO.setEmail(member.getEmail());

                log.debug("CustomOAuth2UserService.loadUser() 종료");

                return new CustomOAuth2User(oAuth2LoginDTO, oAuth2Response);
        } catch (Exception e) {
            log.error("OAuth2 인증 처리 중 오류 발생", e);
            throw new OAuth2AuthenticationException("OAuth2 인증 처리 중 오류 발생" + e);
        }
    }
}

