package com.beancontainer.global.service;

import com.beancontainer.domain.member.entity.RefreshToken;
import com.beancontainer.domain.member.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    @Transactional(readOnly = false)
    public RefreshToken addRefreshToken(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }


    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public Optional<RefreshToken> getRefreshTokenByUserId(String userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public void deleteRefreshToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

}