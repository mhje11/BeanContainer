package com.beancontainer.global.auth.jwt.filter;

import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.auth.service.RefreshTokenService;
import com.beancontainer.global.auth.jwt.token.JwtAuthenticationToken;
import com.beancontainer.global.auth.service.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getToken(request, "accessToken");
        String refreshToken = getToken(request, "refreshToken");

        try {
            if (StringUtils.hasText(accessToken)) {
                Authentication authentication = getAuthentication(accessToken);
                log.debug("Authentication: {}", authentication);
                log.debug("User authorities: {}", authentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (StringUtils.hasText(refreshToken)) {
                handleRefreshToken(request, response, refreshToken);
            }
        } catch (ExpiredJwtException e) {
            handleExpiredToken(request, response, refreshToken);
        } catch (Exception e) {
            log.error("JWT 처리 중 오류 발생", e);
        }

        filterChain.doFilter(request, response);
    }

    private void handleRefreshToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        if (refreshTokenService.isRefreshTokenValid(refreshToken) && !jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
            String newAccessToken = jwtTokenizer.newAccessToken(refreshToken);
            setAccessTokenCookie(response, newAccessToken);
            Authentication authentication = getAuthentication(newAccessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        if (StringUtils.hasText(refreshToken) && !jwtTokenizer.isRefreshTokenExpired(refreshToken)) {
            String newAccessToken = jwtTokenizer.newAccessToken(refreshToken);
            setAccessTokenCookie(response, newAccessToken);
            Authentication authentication = getAuthentication(newAccessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

    private Authentication getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        log.debug("Parsed token - userId: {}, role: {}", userId, role);
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
        CustomUserDetails userDetails = new CustomUserDetails(userId, "");
        return new JwtAuthenticationToken(authorities, userDetails, null);
    }

    private List<GrantedAuthority> getGrantedAuthorities(String role) {
        if (role != null && role.startsWith("ROLE_")) {
            return Collections.singletonList(new SimpleGrantedAuthority(role));
        } else {
            return Collections.emptyList();
        }
    }
    private String getToken(HttpServletRequest request, String tokenName) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (tokenName.equals(cookie.getName())) {
                    log.info("쿠키 정보: {}", Arrays.toString(request.getCookies()));
                    return cookie.getValue();
                }
            }
        }
        log.info("토큰 없음 !! 토큰 이름: {}, 쿠키 정보: {}", tokenName, Arrays.toString(cookies));
        return null;
    }

    private void setAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setPath("/");
        cookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
        log.info("setAccessCookie : " + accessToken);
        response.addCookie(cookie);
    }
}