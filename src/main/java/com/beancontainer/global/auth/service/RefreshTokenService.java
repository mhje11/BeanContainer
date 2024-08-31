package com.beancontainer.global.auth.service;


import com.beancontainer.global.auth.jwt.repository.RefreshTokenRepository;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
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
    private final JwtTokenizer jwtTokenizer;
    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtTokenizer jwtTokenizer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenizer = jwtTokenizer;
    }


    //RefreshToken 저장
    @Transactional
    public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }


    //DB에서 RefreshToken 값 조회
    public Optional<RefreshToken> findByRefresh(String refresh) {
        return refreshTokenRepository.findByRefresh(refresh);
    }

    //accessToken 재발급시 DB에 있는 refreshToken 업데이트
    @Transactional
    public void updateRefreshToken(RefreshToken refreshToken, String newRefreshTokenValue) {
        refreshToken.updateRefresh(newRefreshTokenValue);
        refreshTokenRepository.save(refreshToken);
    }


    //RefreshToken 삭제
    @Transactional
    public void deleteRefreshToken(String refresh) {
        refreshTokenRepository.deleteByRefresh(refresh);
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return findByRefresh(refreshToken).isPresent() && !jwtTokenizer.isRefreshTokenExpired(refreshToken);
    }




}