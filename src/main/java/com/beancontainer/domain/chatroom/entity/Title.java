package com.beancontainer.domain.chatroom.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Title {
    @Id
    private String name;

}
