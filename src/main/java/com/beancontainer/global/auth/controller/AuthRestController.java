package com.beancontainer.global.auth.controller;

import com.beancontainer.domain.member.dto.LoginRequestDTO;
import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.dto.VerifyCodeDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.AuthService;
import com.beancontainer.domain.member.service.MailService;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.auth.service.RefreshTokenService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {
    private final AuthService authService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final MailService mailService;


    //리프레시 토큰 수정 필요
    @PostMapping("/login")
    public ResponseEntity<LoginRequestDTO> login(@RequestBody @Valid LoginRequestDTO userLoginRequestDto,
                                     BindingResult bindingResult, HttpServletResponse response) {
        log.info("==login==");
        //username, password가 null 일 때
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // username 과 password 값을 잘 받아왔다면
        // 우리 서버의 저장되어 있는 유저인지 확인
        Member member = memberService.findByUserId(userLoginRequestDto.getUserId());
        //요청 정보에서 얻어온 비밀번호와 서버의 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), member.getPassword())) {
            //비밀번호가 일치하지 않을 때
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }

        //탈퇴한 계정
        if (member.getDeletedAt() != null) {
            throw new CustomException(ExceptionCode.CANCEL_ACCOUNT);
        }

        //토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(member);
        String refreshToken = jwtTokenizer.createRefreshToken(member);
        log.info("Access token created: {}", accessToken.substring(0, Math.min(accessToken.length(), 10)));
        log.info("Refresh token created: {}", refreshToken.substring(0, Math.min(refreshToken.length(), 10)));


        //리프레시토큰을 디비에 저장.
        RefreshToken refreshTokenEntity = new RefreshToken();


        refreshTokenService.addRefreshToken(refreshTokenEntity);

        // 로그인 성공 로그 출력
        log.info("User {} logged in successfully.", userLoginRequestDto.getUserId());

        //응답으로 보낼 값들을 준비해요.
        LoginRequestDTO loginResponseDto = LoginRequestDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(String.valueOf(member.getId()))
                .name(member.getName())
                .build();

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);  //보안 (쿠키값을 자바스크립트같은곳에서는 접근할수 없어요.)
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000)); //7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }


    //RefreshToken 을 통한 토큰 재발급
    //변경 필요 -> 쿠키에 토큰이 만료가 되었다면 리프레쉬를 통해 액세스를 재발급 해주면 됨
    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키로부터 refresh Token 얻어 옴
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        // 없으면 오류 응답
        /**
         * 수정 필요
         */
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found");
        }

        try {
            Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
            String userId = claims.getSubject();

            Member member = memberService.findByUserId(userId);
            if (member == null) {
                throw new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
            }

            // 새로운 access token 생성
            String newAccessToken = jwtTokenizer.createAccessToken(member);

            // 새로운 refresh token 생성 (선택적)
            String newRefreshToken = jwtTokenizer.createRefreshToken(member);

            // 기존 refresh token 삭제 및 새로운 refresh token 저장
            refreshTokenService.deleteRefreshToken(refreshToken);
            RefreshToken newRefreshTokenEntity = new RefreshToken();
            refreshTokenService.addRefreshToken(newRefreshTokenEntity);

            // 새로운 refresh token을 쿠키에 설정
            Cookie newRefreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
            newRefreshTokenCookie.setHttpOnly(true);
            newRefreshTokenCookie.setSecure(true); // HTTPS에서만 전송
            newRefreshTokenCookie.setPath("/");
            newRefreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));
            newRefreshTokenCookie.setAttribute("SameSite", "Strict");
            response.addCookie(newRefreshTokenCookie);

            // Access token은 응답 본문에 포함
            Map<String, String> tokenResponse = new HashMap<>();
            tokenResponse.put("accessToken", newAccessToken);
            tokenResponse.put("userId", userId);
            tokenResponse.put("name", member.getName());

            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken != null) {
            //DB에서 삭제
            refreshTokenService.deleteRefreshToken(refreshToken);


            //쿠키에서도 accessToken, refreshToken 전부 삭제 함
            Cookie accessTokenCookie = new Cookie("accessToken", null);
            accessTokenCookie.setMaxAge(0);
            accessTokenCookie.setPath("/");

            Cookie refreshTokenCookie = new Cookie("refreshToken", null);
            refreshTokenCookie.setMaxAge(0);
            refreshTokenCookie.setPath("/");

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            log.info("User logged out successfully.");
            return ResponseEntity.ok("로그아웃되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("Refresh token not found");
        }
    }

}

