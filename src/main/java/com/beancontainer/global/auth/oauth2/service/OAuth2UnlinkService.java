package com.beancontainer.global.auth.oauth2.service;


import com.beancontainer.global.auth.jwt.service.CookieService;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UnlinkService {

    private final WebClient webClient;
    private final CookieService cookieService;
    private final JwtTokenizer jwtTokenizer;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    public Mono<Void> unlinkAccount(String userId, HttpServletRequest request) {
        String accessToken = cookieService.getCookieValue(request, "accessToken");
        if (accessToken == null || accessToken.isEmpty()) {
            return Mono.error(new CustomException(ExceptionCode.UNAUTHORIZED));
        }

        Claims claims = jwtTokenizer.parseAccessToken(accessToken);
        String provider = claims.get("provider", String.class);

        if (provider == null || provider.isEmpty()) {
            return Mono.error(new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER));
        }

        return switch (provider.toLowerCase()) {
            case "naver" -> unlinkNaver(accessToken);
            case "kakao" -> unlinkKakao(accessToken);
            default -> Mono.error(new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER));
        };
    }

    private Mono<Void> unlinkNaver(String accessToken) {
        return webClient.post()
                .uri("https://nid.naver.com/oauth2.0/token")
                .bodyValue("client_id=" + naverClientId +
                        "&client_secret=" + naverClientSecret +
                        "&access_token=" + accessToken +
                        "&grant_type=delete")
                .retrieve()
                .bodyToMono(Void.class);
    }

    private Mono<Void> unlinkKakao(String accessToken) {
        return webClient.post()
                .uri("https://kapi.kakao.com/v1/user/unlink")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Void.class);
    }

}