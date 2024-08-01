package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.dto.MemberSignUpDto;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor //생성자 자동 주입
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; //패스워드 암호화


    @Transactional
    public Member signUp(MemberSignUpDto signUp) {
        //아이디가 중복 체크
        if(memberRepository.findByUserId(signUp.getUserId()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 아이디 입니다.");
        }

        Member member = Member.createMember(
                signUp.getName(),
                signUp.getNickname(),
                signUp.getUserId(),
                passwordEncoder.encode(signUp.getPassword()) //DTO의 password 암호화
        );
        return memberRepository.save(member);
    }
}
