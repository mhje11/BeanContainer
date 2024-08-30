package com.beancontainer.global.auth.controller;

import com.beancontainer.domain.member.dto.LoginRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.auth.service.RefreshTokenService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
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


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRestController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;


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

        //리프레시토큰을 디비에 저장.
        RefreshToken refreshTokenEntity = new RefreshToken(member.getUserId(), refreshToken);
        refreshTokenService.saveRefreshToken(refreshTokenEntity);


        // Access Token을 쿠키에 설정
        addCookie(response, "accessToken", accessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        // Refresh Token을 쿠키에 설정
        addCookie(response, "refreshToken", refreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));


        //응답으로 보낼 값
        LoginRequestDTO loginResponseDto = LoginRequestDTO.builder()
                .userId(String.valueOf(member.getId()))
                .name(member.getName())
                .build();

        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }


    //RefreshToken 을 통한 토큰 재발급
    //변경 필요 -> 쿠키에 토큰이 만료가 되었다면 리프레쉬를 통해 액세스를 재발급 해주면 됨
    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refreshToken");

        //refreshToken 이 없다면 401 코드 보냄
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token not found");
        }

        try {
            RefreshToken storedToken = refreshTokenService.findByRefresh(refreshToken)
                    .orElseThrow(() -> new CustomException(ExceptionCode.INVALID_REFRESH_TOKEN));

            Member member = memberService.findByUserId(storedToken.getUserId());
            String newAccessToken = jwtTokenizer.createAccessToken(member);
            String newRefreshToken = jwtTokenizer.createRefreshToken(member);

            refreshTokenService.updateRefreshToken(storedToken, newRefreshToken);

            // 새로운 Access Token을 쿠키에 설정
            addCookie(response, "accessToken", newAccessToken, (int) (JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

            // 새로운 Refresh Token을 쿠키에 설정
            addCookie(response, "refreshToken", refreshToken, (int) (JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000));


            // 기존 refresh token 삭제 및 새로운 refresh token 저장
            refreshTokenService.deleteRefreshToken(refreshToken);
            RefreshToken newRefreshTokenEntity = new RefreshToken(member.getUserId(), newRefreshToken);
            refreshTokenService.addRefreshToken(newRefreshTokenEntity);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getCookieValue(request, "refreshToken");

        if (refreshToken != null) {
            //DB에서 삭제
            refreshTokenService.deleteRefreshToken(refreshToken);

            // Access Token 쿠키 만료
            addCookie(response, "accessToken", "", 0);

            // Refresh Token 쿠키 만료
            addCookie(response, "refreshToken", "", 0);

            return ResponseEntity.ok("로그아웃되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("Refresh token not found");
        }
    }

    //쿠키에 토큰 추가
    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge); //유효기간 따로 지정해주기
        response.addCookie(cookie);
    }

    //쿠키에서 값 추출 함
    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

