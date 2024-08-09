package com.beancontainer.domain.member.service;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.memberprofileimg.service.ProfileImageService;
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
    private final ProfileImageService profileImageService;



    //ID로 유저 찾기
    public Member findByUserId(String userId) {
        return memberRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public void updateNickname(String userId, String newNickname) {
        Member member = findByUserId(userId);
        member.updateNickname(newNickname);
        memberRepository.save(member);
    }


    @Transactional
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }



    @Transactional
    public void deleteAccount(String userId) {
        Member member = findByUserId(userId);
        memberRepository.delete(member);
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




}
