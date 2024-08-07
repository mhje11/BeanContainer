package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.LoginDTO;
import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.RefreshToken;
import com.beancontainer.domain.member.service.AuthService;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.jwt.util.JwtUtil;
import com.beancontainer.global.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, BindingResult bindingResult, HttpServletResponse response) {
        log.info("Login request received for user: {}", loginDTO.getUserId());
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        //유저가 있는지 확인 후 password 일치 여부 확인
        Member member = memberService.findByUserId(loginDTO.getUserId());
        if(!passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
            return new ResponseEntity("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }


        //jwt 토큰 발급
        String accessToken = jwtUtil.createAccessToken(
                member.getId(), member.getUserId(), member.getName(), member.getNickname(), member.getRole());
        String refreshToken = jwtUtil.createRefreshToken(
                member.getId(), member.getUserId(), member.getName(), member.getNickname(), member.getRole());

        log.info("Access Token (앞 7자리): {}",
                accessToken.substring(0, Math.min(accessToken.length(), 7)));

        //DB에 refreshToken 저장
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(member.getUserId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        //응답 보내줌
        LoginDTO loginResponseDTO = loginDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(member.getUserId())
                .name(member.getName())
                .build();

        //보안을 위해
        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
        accessTokenCookie.setHttpOnly(true);  //http only 로 JS에서 접근 불가능
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(jwtUtil.ACCESS_TOKEN_EXPIRE_COUNT/1000)); //30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(jwtUtil.REFRESH_TOKEN_EXPIRE_COUNT/1000)); //7일

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);


        return new ResponseEntity(loginDTO, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        log.info("Received signup request for user: " + signUpRequestDTO.getUserId());
        authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

}