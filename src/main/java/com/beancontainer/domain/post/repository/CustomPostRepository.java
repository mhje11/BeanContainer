package com.beancontainer.domain.post.repository;

import com.beancontainer.domain.post.dto.PostListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomPostRepository {
    Page<PostListResponseDto> getAllPosts(Pageable pageable, String sortBy);
}
