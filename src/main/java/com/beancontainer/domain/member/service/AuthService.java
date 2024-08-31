package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.auth.service.RefreshTokenService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//인증 관련 비즈니스 로직
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public void signUp(SignUpRequestDTO signUpRequestDTO) {
        if(memberRepository.findByUserId(signUpRequestDTO.getUserId()).isPresent()) {
            throw new CustomException(ExceptionCode.DUPLICATE_USER_ID);
        }

        //memder 객체 생성
        Member member = Member.createMember(
                signUpRequestDTO.getName(),
                signUpRequestDTO.getNickname(),
                signUpRequestDTO.getUserId(),
                passwordEncoder.encode(signUpRequestDTO.getPassword()),
                signUpRequestDTO.getEmail()
        );

        //db에 저장
        Member savedMember = memberRepository.save(member);
        log.info("==== 새로운 유저 회원가입!: {}", signUpRequestDTO.getUserId() + " ====");

    }
    @Transactional
    public String[] login(String userId, String password) {
        Member member = memberService.findByUserId(userId);
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }
        if (member.getDeletedAt() != null) {
            throw new CustomException(ExceptionCode.CANCEL_ACCOUNT);
        }

        String accessToken = jwtTokenizer.createAccessToken(member.getId(), member.getUserId(), member.getName(), member.getRole().name());
        String refreshToken = jwtTokenizer.createRefreshToken(member.getId(), member.getUserId(), member.getName(), member.getRole().name());

        RefreshToken refreshTokenEntity = new RefreshToken(member.getUserId(), refreshToken);
        refreshTokenService.saveRefreshToken(refreshTokenEntity);

        return new String[]{accessToken, refreshToken};
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED);
        }
        refreshTokenService.deleteRefreshToken(refreshToken);
    }



}
