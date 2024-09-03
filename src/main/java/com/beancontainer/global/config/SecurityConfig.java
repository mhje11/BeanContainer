package com.beancontainer.global.config;

import com.beancontainer.global.auth.jwt.filter.JwtAuthenticationFilter;
import com.beancontainer.global.auth.jwt.util.JwtTokenizer;
import com.beancontainer.global.auth.oauth2.handler.CustomSuccessHandler;
import com.beancontainer.global.auth.service.CustomOAuth2UserService;
import com.beancontainer.global.auth.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.sql.Ref;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtTokenizer jwtTokenizer;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final RefreshTokenService refreshTokenService;

    public SecurityConfig(JwtTokenizer jwtTokenizer, CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, RefreshTokenService refreshTokenService) {
        this.jwtTokenizer = jwtTokenizer;
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.refreshTokenService = refreshTokenService;
    }

    //모든 유저 허용
    String[] allAllowPage = new String[]{
            "/", "/login", "/signup", //메인, 로그인, 회원가입
            "/js/**", "/css/**", "/images/**", "/static/**", //resources
            "/api/auth/**", //로그인, 로그아웃, 회원가입 API 요청
            "/posts/list", "/api/posts/list", "/api/posts/{postId}", "/posts/{postId}", //게시글 조회, 게시글 상세보기는 모두 가능
            "/api/comments/list/{postId}",  // 댓글 목록
            "/review/{kakaoId}", "/api/cafes/{cafeId}/reviews", "/reviewlist/{cafeId}", //리뷰도 모두 조회 가능
            "mymap/{mapId}",
            "api/maps/{mapId}", "api/cafes",
            "/api/cafes/{cafeId}", // 카페정보
            "/api/map/category", //카테고리 저장 후 검색
            "/api/maps/random",
            "/api/reviews",
            "/api/reviews/{reviewId}/delete", "/api/reviews/{reviewId}/update", //리뷰 작성, 수정, 삭제
            "/api/cafes"

    };

    //관리자 페이지
    String[] adminAllowPage = new String[]{
            "/admin/**", "/api/admin/**"
    };

    //인증받은 회원만 접근 가능
    String[] authPage = new String[]{
            "/api/posts", "/api/posts/{postId}/**", //글 작성/수정/삭제
            "/mypage/{userId}", "/api/mypage/{userId}", //마이페이지, 프로필 변경
            "/mymap", "/mymap/update/{mapId}",
            "/api/maps/{mapId}/delete", "/api/maps/{mapId}/update", "/api/maps/my", //나만의 지도
            "/chat/**" //모든 채팅
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(configurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                //세션 설정 -> 세션정보를 서버에 저장하지 않도록 함
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allAllowPage).permitAll()
                        .requestMatchers(adminAllowPage).hasAuthority("ADMIN") //접두사 제거
                        .requestMatchers(authPage).authenticated()
                        .anyRequest().authenticated() //그 외 모든 요청은 인증 필요
                )
                //oauth2 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userService(customOAuth2UserService))
                        .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
                        .successHandler(customSuccessHandler))
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenizer, refreshTokenService), UsernamePasswordAuthenticationFilter.class);
        http
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .deleteCookies("accessToken", "refreshToken")
                        .permitAll()
                );


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    public CorsConfigurationSource configurationSource(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://43.202.33.1:8080/");
//        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true); // 쿠키 허용을 위해 필요
        config.setAllowedMethods(List.of("GET","POST","DELETE", "PUT"));
        source.registerCorsConfiguration("/**",config);
        return source;
    }



}
