package com.beancontainer.global.auth.service;


import com.beancontainer.global.auth.jwt.repository.RefreshTokenRepository;
import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Autowired;
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
        return refreshTokenRepository.findByRefresh(userId);
    }

    public void deleteRefreshToken(String userId) {
        refreshTokenRepository.deleteByRefresh(userId);
    }

    public void saveRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

}