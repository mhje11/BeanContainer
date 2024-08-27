package com.beancontainer.domain.member.service;

import com.beancontainer.domain.comment.repository.CommentRepository;
import com.beancontainer.domain.like.entity.Likes;
import com.beancontainer.domain.like.repository.LikeRepository;
import com.beancontainer.domain.map.entity.Map;
import com.beancontainer.domain.map.repository.MapRepository;
import com.beancontainer.domain.mapcafe.entity.MapCafe;
import com.beancontainer.domain.mapcafe.repository.MapCafeRepository;
import com.beancontainer.domain.member.dto.MemberDTO;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.domain.post.repository.PostRepository;
import com.beancontainer.domain.review.entity.Review;
import com.beancontainer.domain.review.repository.ReviewRepository;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor //생성자 자동 주입
@Slf4j
@Transactional(readOnly = true)
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MapCafeRepository mapCafeRepository;
    private final MapRepository mapRepository;
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;


    //ID로 유저 찾기
    public Member findByUserId(String userId) {
        log.info("Finding member for userId: {}", userId);
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
        log.info("Error : ", userId);
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

    @Transactional
    public void cancelAccount(String userId) {
        Member existMember = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        existMember.cancelAccount();
        memberRepository.save(existMember);
    }

    @Transactional
    @Scheduled(cron = "0 0 3 * * *")
    public void autoDeleteInactiveMember() {
        LocalDateTime threeYearsAgo = LocalDateTime.now().minusYears(3);

        List<Member> inactiveMember = memberRepository.findAllByDeletedAtBefore(threeYearsAgo);
        for (Member member : inactiveMember) {
            commentRepository.deleteByMemberId(member.getId());
            List<Likes> likes = likeRepository.findAllByMember(member);
            likeRepository.deleteAll(likes);
            postRepository.deleteByMemberId(member.getId());

            List<Map> maps = mapRepository.findAllByMember(member);
            for (Map map : maps) {
                List<MapCafe> mapCafes = mapCafeRepository.findAllByMapId(map.getId());
                mapCafeRepository.deleteAll(mapCafes);
                mapRepository.delete(map);
            }

            List<Review> reviews = reviewRepository.findAllByMember(member);
            reviewRepository.deleteAll(reviews);

            memberRepository.delete(member);
        }
    }
}
