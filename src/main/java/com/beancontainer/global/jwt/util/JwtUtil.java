package com.beancontainer.global.jwt.util;

import com.beancontainer.domain.member.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

//JWT 유틸리티
@Component
public class JwtUtil {
    private final SecretKey accessSecret;
    private final SecretKey refreshSecret;

    public static final long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L; // 30분
    public static final long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 7일

    public JwtUtil(@Value("${jwt.secretKey}") String accessSecret,
                        @Value("${jwt.refreshKey}") String refreshSecret) {
        this.accessSecret = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8));
        this.refreshSecret = Keys.hmacShaKeyFor(refreshSecret.getBytes(StandardCharsets.UTF_8));
    }
    //토큰 생성
    private String createToken(Long id, String userId, String name, String nickname, Role role,
                               long expireTime, SecretKey secretKey) {
        Claims claims = Jwts.claims();
        claims.put("id", id);
        claims.put("userId", userId);
        claims.put("name", name);
        claims.put("nickname", nickname);
        claims.put("role", role.name());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createAccessToken(Long id, String userId, String name, String nickname, Role role) {
        return createToken(id, userId, name, nickname, role, ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }


    public String createRefreshToken(Long id, String userId, String name, String nickname, Role role) {
        return createToken(id, userId, name, nickname, role, REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }

    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }

    private Claims parseToken(String token, SecretKey secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}