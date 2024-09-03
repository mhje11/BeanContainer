package com.beancontainer.global.auth.controller;

import com.beancontainer.domain.member.dto.LoginRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.AuthService;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.auth.service.CookieService;
import com.beancontainer.global.auth.service.RefreshTokenService;
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


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;
    private final CookieService cookieService;


    @PostMapping("/login")
    public ResponseEntity<LoginRequestDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO,
                                                 BindingResult bindingResult, HttpServletResponse response) {
        log.debug("Login attempt for user: {}", loginRequestDTO.getUserId());
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String[] tokens = authService.login(loginRequestDTO.getUserId(), loginRequestDTO.getPassword());
        String accessToken = tokens[0];
        String refreshToken = tokens[1];

        cookieService.addCookie(response, "accessToken", accessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        log.info("AccessToken : " + accessToken);
        cookieService.addCookie(response, "refreshToken", refreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));
        log.info("RefreshToken : " + refreshToken);
        Member member = memberService.findByUserId(loginRequestDTO.getUserId());

        LoginRequestDTO loginResponseDto = LoginRequestDTO.builder()
                .userId(String.valueOf(member.getId()))
                .name(member.getName())
                .build();

        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }


    //RefreshToken 을 통한 토큰 재발급
    @PostMapping("/refreshToken")
    public ResponseEntity<RefreshToken> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getCookieValue(request, "refreshToken");
        log.info("기존 리프레쉬 토큰 : " + refreshToken);

        String[] tokens = refreshTokenService.refreshToken(refreshToken);
        String newAccessToken = tokens[0];
        String newRefreshToken = tokens[1];

        cookieService.addCookie(response, "accessToken", newAccessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        cookieService.addCookie(response, "refreshToken", newRefreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieService.getCookieValue(request, "refreshToken");
        authService.logout(refreshToken);

        cookieService.deleteCookie(response, "accessToken");
        cookieService.deleteCookie(response, "refreshToken");

        return ResponseEntity.ok("로그아웃되었습니다.");
    }


}

