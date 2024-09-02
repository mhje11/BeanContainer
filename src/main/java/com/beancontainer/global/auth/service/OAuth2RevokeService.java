//package com.beancontainer.global.auth.service;
//
//import com.beancontainer.domain.member.service.MemberService;
//import com.beancontainer.global.exception.CustomException;
//import com.beancontainer.global.exception.ExceptionCode;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.StandardCharsets;
//
////소셜 회원 탈퇴 service
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class OAuth2RevokeService {
//    private final MemberService memberService;
//
//    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
//    private String naverClientId;
//
//    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
//    private String naverClientSecret;
//
//    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
//    private String naverTokenUri;
//
//    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
//    private String kakaoUnlinkUri;
//
//    @Value("${spring.security.oauth2.client.re.registration.naver.user-info-uri}")
//
//    public void revokeOAuth2Account(String userId) throws IOException {
//        String provider = memberService.getProviderByUserId(userId);
//        if (provider == null) {
//            throw new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER);
//        }
//
//        String accessToken = memberService.getOAuth2AccessToken(userId);
//
//        if ("naver".equalsIgnoreCase(provider)) {
//            revokeNaverAccount(accessToken);
//        } else if ("kakao".equalsIgnoreCase(provider)) {
//            revokeKakaoAccount(accessToken);
//        } else {
//            log.error("Unsupported OAuth2 provider: {}", provider);
//            throw new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER);
//        }
//
//        log.info("Successfully revoked OAuth2 account for user: {} with provider: {}", userId, provider);
//    }
//
//    private void revokeNaverAccount(String accessToken) throws IOException {
//        URL url = new URL(naverTokenUri);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setDoOutput(true);
//
//        String params = String.format("grant_type=delete&client_id=%s&client_secret=%s&access_token=%s&service_provider=NAVER",
//                naverClientId, naverClientSecret, accessToken);
//
//        try (OutputStream os = connection.getOutputStream()) {
//            byte[] input = params.getBytes(StandardCharsets.UTF_8);
//            os.write(input, 0, input.length);
//        }
//
//        int responseCode = connection.getResponseCode();
//        if (responseCode != HttpURLConnection.HTTP_OK) {
//            log.error("Failed to revoke Naver account. Response code: {}", responseCode);
//            throw new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER);
//        }
//
//        connection.disconnect();
//    }
//
//    private void revokeKakaoAccount(String accessToken) throws IOException {
//        URL url = new URL(kakaoUnlinkUri);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setRequestProperty("Authorization", "Bearer " + accessToken);
//
//        int responseCode = connection.getResponseCode();
//        if (responseCode != HttpURLConnection.HTTP_OK) {
//            log.error("Failed to revoke Kakao account. Response code: {}", responseCode);
//            throw new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER);
//        }
//
//        connection.disconnect();
//    }
//}