package com.beancontainer.global.jwt.filter;

import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.entity.Role;
import com.beancontainer.domain.member.repository.MemberRepository;
import com.beancontainer.global.jwt.exception.JwtExceptionCode;
import com.beancontainer.global.jwt.token.JwtAuthenticationToken;
import com.beancontainer.global.jwt.util.JwtUtil;
import com.beancontainer.global.service.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (StringUtils.hasText(token)) {
            try {
                // 변경된 부분: getAuthentication 메서드 호출 후 Authentication 객체 설정
                Authentication authentication = getAuthentication(token);
                if (authentication != null) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.EXPIRED_TOKEN.getCode());
                log.error("Expired Token : {}", token, e);
                throw new BadCredentialsException("Expired token exception", e);
            } catch (UnsupportedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.UNSUPPORTED_TOKEN.getCode());
                log.error("Unsupported Token: {}", token, e);
                throw new BadCredentialsException("Unsupported token exception", e);
            } catch (MalformedJwtException e) {
                request.setAttribute("exception", JwtExceptionCode.INVALID_TOKEN.getCode());
                log.error("Invalid Token: {}", token, e);
                throw new BadCredentialsException("Invalid token exception", e);
            } catch (IllegalArgumentException e) {
                request.setAttribute("exception", JwtExceptionCode.NOT_FOUND_TOKEN.getCode());
                log.error("Token not found: {}", token, e);
                throw new BadCredentialsException("Token not found exception", e);
            } catch (Exception e) {
                log.error("JWT Filter - Internal Error: {}", token, e);
                throw new BadCredentialsException("JWT filter internal exception", e);
            }
        }
        filterChain.doFilter(request, response);
    }

    // 변경된 부분: Authentication 객체를 반환하는 메서드로 변경
    private Authentication getAuthentication(String token) {
        Claims claims = jwtUtil.parseAccessToken(token);

        String userId = claims.get("userId", String.class);
        String roleName = claims.get("role", String.class);
        Role role = Role.valueOf(roleName);

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        CustomUserDetails userDetails = new CustomUserDetails(member, role);

        // 변경된 부분: principal을 userDetails로 설정
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
