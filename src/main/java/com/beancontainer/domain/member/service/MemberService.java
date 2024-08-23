package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor //생성자 자동 주입
@Slf4j
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;


    //ID로 유저 찾기
    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    //닉네임 변경
    @Transactional
    public void updateNickname(String userId, String newNickname) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        member.updateNickname(newNickname);
        memberRepository.save(member);
        log.info("사용자 {} 의 닉네임이 {}로 변경되었습니다.", userId, newNickname);
    }


    //계정 삭제
    @Transactional
    public void deleteAccount(String userId) {
        Member member = findByUserId(userId);
        memberRepository.delete(member);
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        log.info("Error : " , userId);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        return User.builder()
                .username(member.getUserId())
                .password(member.getPassword())
                .roles("USER")
                .build();
    }

    //유저 존재 여부
    public boolean existsByUserId(String userId) {
        return memberRepository.existsByUserId(userId);
    }
}
