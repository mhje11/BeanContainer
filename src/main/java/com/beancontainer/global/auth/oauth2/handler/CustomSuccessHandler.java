package com.beancontainer.global.auth.oauth2.handler;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import com.beancontainer.global.auth.oauth2.dto.CustomOAuth2User;
import com.beancontainer.global.auth.service.RefreshTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//OAuth2 로그인 성공 시 JWT 발급 해주는 클래스
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;
    private final RefreshTokenService refreshTokenService;


    public CustomSuccessHandler(JwtTokenizer jwtTokenizer, MemberRepository memberRepository, RefreshTokenService refreshTokenService) {
        this.jwtTokenizer = jwtTokenizer;
        this.memberRepository = memberRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //OAuth2 User 주입
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String userId = customUserDetails.getUserId();

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        //accessToken, RefreshToken 발급
        String accessToken = jwtTokenizer.createAccessToken(member);
        String refreshToken = jwtTokenizer.createRefreshToken(member);

        //발급받은 토큰을 쿠키에 저장
        // Access Token 쿠키 설정
        addCookie(response, "accessToken", accessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        // Refresh Token 쿠키 설정
        addCookie(response, "refreshToken", refreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));

        // Refresh Token DB 저장
        RefreshToken refreshTokenEntity = new RefreshToken(String.valueOf(member.getId()), refreshToken);
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        // 로그인 성공 후 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, "/");

    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS를 사용하는 경우에만 true로 설정
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

}
