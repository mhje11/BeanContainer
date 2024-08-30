package com.beancontainer.global.auth.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String refresh;

    @Column(name = "expiration_time")
    private Instant expiration; // 만료시간

    public RefreshToken(String userId, String refresh) {
        this.userId = userId;
        this.refresh = refresh;
        this.expiration = Instant.now().plusSeconds(7 * 24 * 60 * 60); // 7일 후 만료
    }

    //업데이트 후 만료 시간 재설정
    //로그인 연장 가능
    public void updateRefresh(String newRefreshToken) {
        this.refresh = newRefreshToken;
        this.expiration = Instant.now().plusSeconds(7 * 24 * 60 * 60); // 7일 후 만료
    }


}
