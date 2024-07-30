package com.beancontainer.domain.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Member {
    @Id
    private Long id; //주석

    private String name; //이름

    private String email;
}
