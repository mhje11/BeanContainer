package com.beancontainer.global.jwt.util;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

// JWT 유틸리티
@Component
@Slf4j
public class JwtTokenizer {
    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public static Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; //30분
    public static Long REFRESH_TOKEN_EXPIRE_COUNT=7*24*60*60*1000L; //7일

    public JwtTokenizer(@Value("${jwt.secretKey}") String accessSecret, @Value("${jwt.refreshKey}") String refreshSecret){
        this.accessSecret = accessSecret.getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = refreshSecret.getBytes(StandardCharsets.UTF_8);
    }

    // 토큰 생성
    private String createToken(String userId, String name, String role, long expire, byte[] secretKey) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("name", name)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public static Key getSigningKey(byte[] secretKey){
        return Keys.hmacShaKeyFor(secretKey);
    }


    public String createAccessToken(Member member) {
        return createToken(
                member.getUserId(),
                member.getName(),
                member.getRole().name(),
                ACCESS_TOKEN_EXPIRE_COUNT,
                accessSecret
        );
    }

    public String createRefreshToken(Member member) {
        return createToken(
                member.getUserId(),
                member.getName(),
                member.getRole().name(),
                REFRESH_TOKEN_EXPIRE_COUNT,
                refreshSecret
        );
    }



    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    private Claims parseToken(String token, byte[] secretKey) {
        log.debug("Parsing token: {}", token.substring(0, Math.min(token.length(), 10)));
        log.debug("Using secret key: {}", new String(secretKey, 0, Math.min(secretKey.length, 10)));

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error parsing token", e);
            throw e;
        }
    }

    public boolean validateTokenHeader(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                return false;
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String header = new String(decoder.decode(chunks[0]));

            // 헤더에 "alg"와 "typ" 필드가 있는지 확인
            return header.contains("\"alg\"") && header.contains("\"typ\"") && header.contains("\"JWT\"");
        } catch (Exception e) {
            log.error("Error validating token header", e);
            return false;
        }
    }

    public boolean validateToken(String token) {
        if (!validateTokenHeader(token)) {
            return false;
        }

        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(accessSecret))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }



}