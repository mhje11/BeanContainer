package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.dto.LoginDTO;
import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.member.repository.RefreshTokenRepository;
import com.beancontainer.global.jwt.util.JwtTokenizer;
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
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenRepository refreshTokenRepository;


    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer, RefreshTokenRepository refreshTokenRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder= passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void signUp(SignUpRequestDTO signUpRequestDTO) {
        if(memberRepository.findByUserId(signUpRequestDTO.getUserId()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 ID 입니다.");
        }

        //memder 객체 생성
        Member member = Member.createMember(
                signUpRequestDTO.getName(),
                signUpRequestDTO.getNickname(),
                signUpRequestDTO.getUserId(),
                passwordEncoder.encode(signUpRequestDTO.getPassword())
        );

        //db에 저장
        memberRepository.save(member);
        log.info("==== 새로운 유저 회원가입!: {}", signUpRequestDTO.getUserId() + " ====");
    }


//    public LoginDTO login(LoginDTO loginDTO) {
//        log.info("로그인 시작: {}", loginDTO.getUserId());
//        Member member = memberRepository.findByUserId(loginDTO.getUserId())
//                .orElseThrow(() -> {
//                    log.warn("ID가 존재하지 않습니다.: {}", loginDTO.getUserId());
//                    return new IllegalArgumentException("Invalid user ID or password");
//                });
//
//        if (!passwordEncoder.matches(loginDTO.getPassword(), member.getPassword())) {
//            log.warn("패스워드가 일치하지 않습니다: {}", loginDTO.getUserId());
//            throw new IllegalArgumentException("Invalid user ID or password");
//        }
//
//        // 토큰 생성
//        String accessToken = jwtTokenizer.createAccessToken(member);
//        String refreshToken = jwtTokenizer.createAccessToken(member);
//
//        log.debug("Generated Access Token (first 10 chars): {}",
//                accessToken.substring(0, Math.min(accessToken.length(), 10)));
//
//        // 로그인 성공 로그 출력
//        log.info("User {} logged in successfully.", loginDTO.getUserId());
//
//        return new LoginDTO(
//                accessToken,
//                refreshToken,
//                member.getUserId(),
//                member.getName(),
//                member.getNickname(),
//                member.getRole()
//        );
//    }

}
