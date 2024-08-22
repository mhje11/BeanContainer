package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.dto.SignUpRequestDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.memberprofileimg.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//인증 관련 비즈니스 로직
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
                passwordEncoder.encode(signUpRequestDTO.getPassword()),
                signUpRequestDTO.getEmail()
        );

        //db에 저장
        Member savedMember = memberRepository.save(member);
        log.info("==== 새로운 유저 회원가입!: {}", signUpRequestDTO.getUserId() + " ====");


    }


}
