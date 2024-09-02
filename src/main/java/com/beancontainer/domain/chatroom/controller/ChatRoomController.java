package com.beancontainer.domain.chatroom.controller;

import com.beancontainer.domain.chatroom.dto.ChatRoomDto;
import com.beancontainer.domain.chatroom.entity.ChatRoom;
import com.beancontainer.domain.chatroom.repository.ChatRoomRepository;
import com.beancontainer.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatRoomRepository chatRoomRepository;

    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> room() {
        return chatRoomService.findAllRoom();
    }

    @PostMapping("/room/{name}/{capacity}")
    @ResponseBody
    public ChatRoomDto createRoom(@PathVariable String name, @PathVariable int capacity, @AuthenticationPrincipal UserDetails userDetails) {
        return chatRoomService.createChatRoom(name, capacity, userDetails.getUsername());
    }

    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = chatRoomService.enterRoom(roomId, userDetails.getUsername());
        model.addAttribute("roomId", roomId);
        model.addAttribute("userId", username);
        return "/chat/roomdetail";
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto roomInfo(@PathVariable Long roomId) {
        return chatRoomService.findRoomById(roomId);
    }

    @PostMapping("/room/{roomId}/exit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exitRoom(@PathVariable Long roomId) {
        chatRoomService.exitRoom(roomId);
    }
}

