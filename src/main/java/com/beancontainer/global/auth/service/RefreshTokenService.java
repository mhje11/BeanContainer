package com.beancontainer.global.auth.service;


import com.beancontainer.global.auth.jwt.repository.RefreshTokenRepository;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public void deleteAndSaveNewRefreshToken(String userId, String oldRefreshToken, String newRefreshToken) {
        // 기존 토큰 삭제
        refreshTokenRepository.deleteByRefresh(oldRefreshToken);

        // 새 토큰 저장
        RefreshToken newToken = new RefreshToken(userId, newRefreshToken);
        refreshTokenRepository.save(newToken);
    }

    //RefreshToken 저장, 업데이트
    @Transactional(readOnly = false)
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    //DB에서 RefreshToken 값 조회
    public Optional<RefreshToken> findByRefresh(String refresh) {
        return refreshTokenRepository.findByRefresh(refresh);
    }

    //RefreshToken 삭제
    @Transactional
    public void deleteRefreshToken(String refresh) {
        refreshTokenRepository.deleteByRefresh(refresh);
    }

    // 새로운 RefreshToken 추가
    @Transactional
    public void addRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }



}