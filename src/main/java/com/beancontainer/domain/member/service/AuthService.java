package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.dto.LoginDTO;
import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.jwt.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//인증 관련 비즈니스 로직
@Service
@Slf4j
@Transactional(readOnly = true)
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.passwordEncoder= passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void signUp(SignUpRequestDTO signUpRequestDTO) {
        if(memberRepository.findByUserId(signUpRequestDTO.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID 입니다.");
        }

        Member member = Member.createMember(
                signUpRequestDTO.getName(),
                signUpRequestDTO.getNickname(),
                signUpRequestDTO.getUserId(),
                passwordEncoder.encode(signUpRequestDTO.getPassword())
        );

        memberRepository.save(member);
    }


    public LoginDTO login(LoginDTO loginDTO) {
        Member member = memberRepository.findByUserId(loginDTO.getUserId())
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent user ID: {}", loginDTO.getUserId());
                    return new IllegalArgumentException("Invalid user ID or password");
                });

        if (!passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
            log.warn("Login attempt with incorrect password for user: {}", loginDTO.getUserId());
            throw new IllegalArgumentException("Invalid user ID or password");
        }

        String accessToken = jwtUtil.createAccessToken(
                member.getId(),
                member.getUserId(),
                member.getName(),
                member.getNickname(),
                member.getRole()
        );

        String refreshToken = jwtUtil.createRefreshToken(
                member.getId(),
                member.getUserId(),
                member.getName(),
                member.getNickname(),
                member.getRole()
        );

        // 토큰의 일부분만 로그로 출력 (보안상 전체 토큰을 로그에 남기지 않음)
        log.debug("Generated Access Token (first 10 chars): {}",
                accessToken.substring(0, Math.min(accessToken.length(), 10)));

        log.info("User logged in successfully: {}", loginDTO.getUserId());

        return new LoginDTO(
                accessToken,
                refreshToken,
                member.getUserId(),
                member.getName(),
                member.getNickname(),
                member.getRole()
        );
    }
}
