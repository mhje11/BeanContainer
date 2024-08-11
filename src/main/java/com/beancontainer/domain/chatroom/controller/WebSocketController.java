package com.beancontainer.domain.chatroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// 기본적인 채팅 화면을 제공

@Controller
public class WebSocketController {

    @GetMapping("/chat")
    public String chat(){
        return "chat";
        //chat template 반환
    }
}