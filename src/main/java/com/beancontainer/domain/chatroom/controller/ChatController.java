package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.entity.ChatListEntity;
import com.beancontainer.domain.chatroom.entity.ChatroomEntity;
import com.beancontainer.domain.chatroom.entity.MessageEntity;
import com.beancontainer.domain.chatroom.service.ChatService;
import com.beancontainer.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MemberService memberService; // 추가

    @GetMapping("/chat")
    public String chatList(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<ChatListEntity> chatList = chatService.getChatList(userDetails.getUsername());
        model.addAttribute("chatList", chatList);
        return "chat/chatlist";
    }

    @GetMapping("/chat/{id}")
    public String chatRoom(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        ChatroomEntity chatroom = chatService.getChatroom(id);
        if (!chatService.hasAccess(userDetails.getUsername(), chatroom)) {
            return "redirect:/welcome";
        }
        List<MessageEntity> messages = chatService.getChatroomMessages(id);
        model.addAttribute("chatroom", chatroom);
        model.addAttribute("messages", messages);
        model.addAttribute("username", userDetails.getUsername());
        return "chat/chatroom";
    }

    @PostMapping("/chat/new")
    public String newChat(@RequestParam String username, @AuthenticationPrincipal UserDetails userDetails) {
        ChatroomEntity chatroom = chatService.createChatroom(userDetails.getUsername(), username);
        return "redirect:/chat/" + chatroom.getId();
    }
}