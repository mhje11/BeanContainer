package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.dto.SignUpDto;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor //생성자 자동 주입
@Slf4j
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; //패스워드 암호화
    private AuthenticationManager authenticationManager; //Security 에서 제공하는 인증 과정 처리

    //회원 가입
    @Transactional
    public Member signUp(SignUpDto signUp) {
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



    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("Error : " , userId);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));

        return User.builder()
                .username(member.getUserId())
                .password(member.getPassword())
                .roles("USER")
                .build();
    }



    public Optional<Member> findByUserId(String userId) {
        return memberRepository.findByUserId(userId);
    }


}
