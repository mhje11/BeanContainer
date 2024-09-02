    package com.beancontainer.domain.post.dto;

    import com.beancontainer.domain.post.entity.Post;
    import lombok.*;

    import java.time.LocalDateTime;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class PostDetailsResponseDto {
        private Long id;
        private String title;
        private String nickname;
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer views;
        private Integer likes;
        private String content;
        private Boolean authorCheck;

        public PostDetailsResponseDto(Post updatedPost, int likesCount, Boolean authorCheck) {
            this.id = updatedPost.getId();
            this.title = updatedPost.getTitle();
            this.nickname = updatedPost.getMember().getNickname();
            this.profileImageUrl = updatedPost.getMember().getProfileImageUrl();
            this.createdAt = updatedPost.getCreatedAt();
            this.updatedAt = updatedPost.getUpdatedAt();
            this.views = updatedPost.getViews();
            this.likes = likesCount;
            this.content = updatedPost.getContent();
            this.authorCheck = authorCheck;
        }
    }
