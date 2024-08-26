    package com.beancontainer.domain.post.dto;

    import com.beancontainer.domain.post.entity.Post;
    import com.beancontainer.domain.postimg.entity.PostImg;
    import lombok.*;

    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.stream.Collectors;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public class PostDetailsResponseDto {
        private Long id;
        private String title;
        private String nickname;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Integer views;
        private Integer likes;
        private String content;
        private List<String> imageUrls;
        private List<Long> imageIds;
        private Boolean authorCheck;

        public PostDetailsResponseDto(Post updatedPost, int likesCount, Boolean authorCheck) {
            this.id = updatedPost.getId();
            this.title = updatedPost.getTitle();
            this.nickname = updatedPost.getMember().getNickname();
            this.createdAt = updatedPost.getCreatedAt();
            this.updatedAt = updatedPost.getUpdatedAt();
            this.views = updatedPost.getViews();
            this.likes = likesCount;
            this.content = updatedPost.getContent();
            this.imageUrls = updatedPost.getImages().stream()
                    .map(PostImg::getPath)
                    .collect(Collectors.toList());
            this.imageIds = updatedPost.getImages().stream()
                    .map(PostImg::getId)
                    .collect(Collectors.toList());
            this.authorCheck = authorCheck;
        }
    }
