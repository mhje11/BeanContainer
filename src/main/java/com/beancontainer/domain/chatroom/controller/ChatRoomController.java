package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> room() {
        return chatRoomService.findAllRoom();
    }

    @PostMapping("/room")
    @ResponseBody
    public ChatRoomDto createRoom(@RequestParam String name, @AuthenticationPrincipal UserDetails userDetails) {
        return chatRoomService.createChatRoom(name, userDetails.getUsername());
    }
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("roomId", roomId);
        model.addAttribute("userId", userDetails.getUsername());
        return "/chat/roomdetail";
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto roomInfo(@PathVariable Long roomId) {
        return chatRoomService.findRoomById(roomId);
    }
}