package com.beancontainer.domain.postimg.entity;

import com.beancontainer.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "post_images")
@NoArgsConstructor
@Getter
public class PostImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID name;  // 이미지 이름

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID path;  // 이미지 경로

    private UUID type;  // 이미지 타입 (jpg, png, ...)

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;
}
