package com.beancontainer.domain.member.service;

import com.beancontainer.domain.chatroom.entity.ChatMessage;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatMessageRepository;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
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
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.auth.jwt.repository.RefreshTokenRepository;
import com.beancontainer.global.auth.service.RefreshTokenService;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor //생성자 자동 주입
@Slf4j
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MapCafeRepository mapCafeRepository;
    private final MapRepository mapRepository;
    private final ReviewRepository reviewRepository;
    private final LikeRepository likeRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final OAuth2AuthorizedClientService authorizedClientService;



    @Transactional(readOnly = true)
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
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        return User.builder()
                .username(member.getUserId())
                .password(member.getPassword())
                .roles("USER")
                .build();
    }

    @Transactional(readOnly = true)
    //유저 존재 여부
    public boolean existsByUserId(String userId) {
        try {
            return memberRepository.existsByUserId(userId);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
        }
    }

    //OAuth2 제공자 정보 불러오기(naver/kakao)
    @Transactional(readOnly = true)
    public String getProviderByUserId(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User not found with userId: {}", userId);
                    return new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
                });

        String provider = member.getProvider();
        log.debug("Provider for userId {}: {}", userId, provider);

        return provider;
    }


    @Transactional(readOnly = true)
    public String getOAuth2AccessToken(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new CustomException(ExceptionCode.MEMBER_NOT_FOUND);
                });

        String provider = member.getProvider();
        log.debug("Provider for user {}: '{}'", userId, provider);

        if (provider == null || provider.trim().isEmpty()) {
            log.error("Provider is null or empty for user: {}", userId);
            throw new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER);
        }

        provider = provider.trim().toLowerCase();
        if (!provider.equals("naver") && !provider.equals("kakao")) {
            log.error("Invalid OAuth2 provider for user {}: '{}'", userId, provider);
            throw new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER);
        }

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(provider, userId);
        if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
            log.error("OAuth2 token not found for user {} with provider {}", userId, provider);
            throw new CustomException(ExceptionCode.INVALID_OAUTH2_PROVIDER);
        }

        return authorizedClient.getAccessToken().getTokenValue();
    }

    // 회원 계정 비활성화
    @Transactional
    public void cancelAccount(String userId) {
        Member existMember = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
        existMember.cancelAccount();

        // RefreshToken 조회 및 삭제
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByUserId(userId);
        refreshTokenOpt.ifPresent(refreshToken ->
                refreshTokenService.deleteRefreshToken(refreshToken.getRefresh())
        );
        //회원 삭제
        memberRepository.save(existMember);
    }

    // 비활성화 후 3년 뒤 자동 삭제
    @Transactional
    @Scheduled(cron = "0 0 3 * * *") //삭제는 새벽 3시에 진행
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

            List<ChatMessage> messages = chatMessageRepository.findAllBySender(member);
            chatMessageRepository.deleteAll(messages);

            List<ChatRoom> chatRooms = chatRoomRepository.findAllByCreator(member);
            chatRoomRepository.deleteAll(chatRooms);

            memberRepository.delete(member);

        }
    }
}
