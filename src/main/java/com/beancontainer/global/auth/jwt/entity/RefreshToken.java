package com.beancontainer.global.auth.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String expiration; // 만료시간

    public RefreshToken(String userId, String refresh) {
        this.userId = userId;
        this.refresh = refresh;
    }


}
