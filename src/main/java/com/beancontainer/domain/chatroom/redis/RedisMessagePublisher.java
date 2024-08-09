package com.beancontainer.domain.chatroom.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {
    private final RedisTemplate<String,Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(String message){
        redisTemplate.convertAndSend(channelTopic.getTopic(),message);
    }
}