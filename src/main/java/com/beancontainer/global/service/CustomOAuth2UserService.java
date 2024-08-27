package com.beancontainer.global.service;

import com.beancontainer.global.oauth2.dto.KakaoResponse;
import com.beancontainer.global.oauth2.dto.NaverResponse;
import com.beancontainer.global.oauth2.dto.OAuth2Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("OAuth2 로그인 : " + oAuth2User);

        // 어떤 소셜 요청 로그인인지 확인
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

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
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
    }
}
