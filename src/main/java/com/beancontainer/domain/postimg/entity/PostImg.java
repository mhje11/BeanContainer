package com.beancontainer.domain.postimg.entity;

import com.beancontainer.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_images")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originName;  // 이미지 파일의 본래 이름

    private String name;  // S3에 저장될 때 이미지 이름

    private String path;  // S3 내부 이미지에 접근할 수 있는 URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public PostImg(String originName, String name, Post post) {
        this.originName = originName;
        this.name = name;
        this.path = "";
        this.post = post;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
