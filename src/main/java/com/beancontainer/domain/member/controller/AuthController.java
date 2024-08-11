package com.beancontainer.domain.member.controller;

import com.beancontainer.domain.member.dto.LoginDTO;
import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.RefreshToken;
import com.beancontainer.domain.member.service.AuthService;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.domain.memberprofileimg.service.ProfileImageService;
import com.beancontainer.global.jwt.util.JwtTokenizer;
import com.beancontainer.global.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final ProfileImageService profileImageService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginDTO userLoginDto,
                                   BindingResult bindingResult, HttpServletResponse response) {
        log.info("==login==");
        //username, password가 null 일 때
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }


        // username 과 password 값을 잘 받아왔다면
        // 우리 서버의 저장되어 있는 유저인지 확인
        Member member = memberService.findByUserId(userLoginDto.getUserId());
        //요청 정보에서 얻어온 비밀번호와 서버의 비밀번호가 일치하는지 확인
        if (!passwordEncoder.matches(userLoginDto.getPassword(), member.getPassword())) {
            //비밀번호가 일치하지 않을 때
            return new ResponseEntity("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
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
        log.info("User {} logged in successfully.", userLoginDto.getUserId());

        //응답으로 보낼 값들을 준비해요.
        LoginDTO loginResponseDto = LoginDTO.builder()
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

        return new ResponseEntity(loginResponseDto, HttpStatus.OK);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
        log.info("Received signup request for user: " + signUpRequestDTO.getUserId());
        authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
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
    public ResponseEntity<?> checkUserId(@RequestParam String userId) {
        try {
            memberService.findByUserId(userId);
            // 사용자를 찾았다면, 이미 존재하는 ID
            return ResponseEntity.ok(true);
        } catch (UsernameNotFoundException e) {
            // 사용자를 찾지 못했다면, 사용 가능한 ID
            return ResponseEntity.ok(false);
        }
    }
}

