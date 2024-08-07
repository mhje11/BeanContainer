//package com.beancontainer.global.jwt.filter;
//
//import com.beancontainer.global.jwt.util.JwtUtil;
//import com.beancontainer.global.service.CustomUserDetails;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import java.util.Collection;
//import java.util.Iterator;
//
//public class LoginFilter extends UsernamePasswordAuthenticationFilter {
//    private final AuthenticationManager authenticationManager;
//    private final JwtUtil jwtUtil;
//
//    public LoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
//        this.authenticationManager = authenticationManager;
//        this.jwtUtil = jwtUtil;
//    }
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//        //클라이언트 요청에서 추출하기
//        String userId = request.getParameter("userId");
//        String password = request.getParameter("password");
//
//        //검증을 위해 token 에 담는다
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, password);
//        //token 에 있는 걸 검증하기 위해 AuthenticationManager 로 전달
//        return authenticationManager.authenticate(authToken);
//    }
//
//    //로그인 성공시 실행 메소드(JWT 토큰 발급)
//    @Override
//    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
//        CustomUserDetails customUserDetails =(CustomUserDetails) authentication.getPrincipal();
//        String username = customUserDetails.getUsername();
//
//        //ROLE 값을 뽑아냄
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
//        GrantedAuthority auth = iterator.next();
//
//        String role = auth.getAuthority();
//
//        //token 만들어 달라고 요청
//        String token = jwtUtil.createJwt(username, role, 60*60*10L);
//        //Header 부분에 담아서 전송함
//        response.addHeader("Authorization", "Bearer" + token);
//
//    }
//
//    //로그인 실패시 실행 메소드
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
//        response.setStatus(401); //실패시 401 오류가 뜨도록!
//    }
//
//}
