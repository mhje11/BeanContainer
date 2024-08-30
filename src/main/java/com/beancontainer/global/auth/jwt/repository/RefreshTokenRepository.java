package com.beancontainer.global.auth.jwt.repository;

import com.beancontainer.global.auth.jwt.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefresh(String value); //token 값 조회

    @Transactional
    void deleteByRefresh(String refresh); //token 삭제



}
