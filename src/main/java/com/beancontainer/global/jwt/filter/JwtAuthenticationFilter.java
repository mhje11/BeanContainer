package com.beancontainer.global.jwt.filter;

import com.beancontainer.global.exception.CustomException;
import com.beancontainer.global.exception.ExceptionCode;
import com.beancontainer.global.jwt.token.JwtAuthenticationToken;
import com.beancontainer.global.jwt.util.JwtTokenizer;
import com.beancontainer.global.service.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenizer jwtTokenizer;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.debug("===== JwtAuthenticationFilter start =====");
        String token = getToken(request);

        if (StringUtils.hasText(token)) {
            try {
                getAuthentication(token);
            } catch (ExpiredJwtException e) {
                log.error("Token expired: {}", token);
                new CustomException(ExceptionCode.JWT_TOKEN_EXPIRED);
                return;
            } catch (Exception e) {
                log.error("Authentication error: ", e);
                new CustomException(ExceptionCode.UNAUTHORIZED);
                return;
            }
        } else {
            log.debug("No token found in request");
        }
//
//        try {
//            String token = getToken(request);
//            if (StringUtils.hasText(token) && jwtTokenizer.validateToken(token)) {
//                Authentication auth = getAuthentication(token);
//                SecurityContextHolder.getContext().setAuthentication(auth);
//            }
//        } catch (ExpiredJwtException e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Token expired");
//            return;
//        } catch (Exception e) {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().write("Authentication failed");
//            return;
//        }

        filterChain.doFilter(request, response);
        log.debug("JwtAuthenticationFilter finished");
    }


    // Authentication 객체를 반환하는 메서드로 변경
    private Authentication getAuthentication(String token) {
        Claims claims = jwtTokenizer.parseAccessToken(token);
        String userId = claims.getSubject();
        String role = claims.get("role", String.class);
        log.debug("Parsed token - userId: {}, role: {}", userId, role);

        List<GrantedAuthority> authorities = getGrantedAuthorities(claims);
        CustomUserDetails userDetails = new CustomUserDetails(userId, role);
        Authentication authentication = new JwtAuthenticationToken(authorities, userDetails, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }


    private List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        String role = claims.get("role", String.class);
        if (role == null) {
            throw new BadCredentialsException("Invalid token: missing role claim");
        }
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private String getToken(HttpServletRequest request) {
        String token = null;

        // 헤더에서 토큰 확인
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            token = authorization.substring(7);
            log.info("Token found in header: {}", token.substring(0, Math.min(token.length(), 10)));
            return token;
        }

        // 쿠키에서 토큰 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    log.info("Token found in cookie: {}", token.substring(0, Math.min(token.length(), 10)));
                    return token;
                }
            }
        }

        log.info("토큰 없음 !! ");
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/oauth2") || path.equals("/login") || path.startsWith("/error");
    }
}
