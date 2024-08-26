package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.LoginRequestDTO;
import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.dto.VerifyCodeDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.RefreshToken;
import com.beancontainer.domain.member.service.AuthService;
import com.beancontainer.domain.member.service.MailService;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import com.beancontainer.global.jwt.util.JwtTokenizer;
import com.beancontainer.global.service.RefreshTokenService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Date;
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

        //토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(member);
        String refreshToken = jwtTokenizer.createRefreshToken(member);
        log.info("Access token created: {}", accessToken.substring(0, Math.min(accessToken.length(), 10)));
        log.info("Refresh token created: {}", refreshToken.substring(0, Math.min(refreshToken.length(), 10)));


        //리프레시토큰을 디비에 저장.
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(String.valueOf(member.getId()));

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
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // 있으면 토큰으로부터 정보 얻어 옴
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        String userId = (String) claims.get("userId");

        Member member = memberService.findByUserId(userId);

        if(member == null) {
            throw new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
        }

        // accessToken 생성.
        String newAccessToken = jwtTokenizer.createAccessToken(member);


        // 쿠키 생성 response로 보내기
        Cookie accessTokenCookie = new Cookie("accessToken", newAccessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000)); // 초 단위로 넘어오니까 밀리로 바꾸기 위해 1000으로 나눔.
        response.addCookie(accessTokenCookie);

        //응답 DTO 생성
        LoginRequestDTO loginResponseDto = LoginRequestDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .userId(userId)
                .name(member.getName())
                .build();

        return ResponseEntity.ok(loginResponseDto);
    }


    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        log.info("Received signup request for user: " + signUpRequestDTO.getUserId());
        authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(Collections.singletonMap("message", "회원가입이 성공적으로 완료되었습니다.")); //JSON 형태로 응답
    }

    //이메일 인증 코드 발송
    @PostMapping("/signup/email-send")
    public ResponseEntity<String> sendEmailCode(@RequestParam(value = "email", required = false) String email) {
        try {
            String authCode = mailService.sendSimpleMessage(email);
            log.info("인증 코드 : " + authCode);
            return ResponseEntity.ok("인증 코드가 전송 되었습니다.");
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.EMAIL_SEND_FAILURE);
        }
    }

    //이메일 인증 코드 확인
    @PostMapping("/signup/check-code")
    public ResponseEntity<String> verifyEmailCode(@RequestBody VerifyCodeDTO verifyCode) {
        log.info("발송 된 인증번호 확인: {}", verifyCode.getCode());

        String authCode = mailService.getAuthNum();
        if (authCode != null && authCode.equals(verifyCode.getCode())) {
            log.info("이메일 인증 성공");
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } else {
            throw new CustomException(ExceptionCode.EMAIL_CODE_MISMATCH);
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

    //아이디 중복 체크
    @GetMapping("/check-userid")
    public ResponseEntity<Map<String, Boolean>> checkUserId(@RequestParam String userId) {
        boolean exists = memberService.existsByUserId(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }
}

