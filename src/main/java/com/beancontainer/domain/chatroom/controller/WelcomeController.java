package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.entity.ChatListEntity;
import com.beancontainer.domain.chatroom.service.ChatService;
import com.beancontainer.domain.member.entity.Member;
import com.beancontainer.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class WelcomeController {

    private final ChatService chatService;
    private final MemberService memberService;

    @GetMapping("/welcome")
    public String welcome(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<ChatListEntity> recentChats = chatService.getRecentChats(userDetails.getUsername());
        model.addAttribute("recentChats", recentChats);
        model.addAttribute("username", userDetails.getUsername());
        return "chat/welcome";
    }

    @PostMapping("/startChat")
    @ResponseBody
    public Map<String, Object> startChat(@AuthenticationPrincipal UserDetails userDetails,
                                         @RequestParam String targetName) {
        Map<String, Object> response = new HashMap<>();

        try {
            Member targetUser = memberService.findByUserId(targetName);
            Long chatroomId = chatService.getOrCreateChatroom(userDetails.getUsername(), targetName);
            response.put("chatroomId", chatroomId);
        } catch (UsernameNotFoundException e) {
            response.put("error", "유효하지 않은 아이디입니다.");
        }

        return response;
    }
}