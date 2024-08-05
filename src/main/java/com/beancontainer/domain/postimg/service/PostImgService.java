package com.beancontainer.domain.postimg.service;

import com.beancontainer.domain.postimg.repository.PostImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostImgService {
    private final PostImgRepository postImgRepository;
}
