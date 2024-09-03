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

    // 채팅방 목록 페이지를 반환
    @GetMapping("/room")
    public String rooms(Model model) {
        return "chat/room";
    }

    // 모든 채팅방의 정보를 JSON 형태로 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoomDto> room() {
        return chatRoomService.findAllRoom();
    }

    // 새로운 채팅방을 생성
    @PostMapping("/room/{name}/{capacity}")
    @ResponseBody
    public ChatRoomDto createRoom(@PathVariable String name, @PathVariable int capacity, @AuthenticationPrincipal UserDetails userDetails) {
        return chatRoomService.createChatRoom(name, capacity, userDetails.getUsername());
    }

    // 특정 채팅방 상세 정보를 반환
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable Long roomId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = chatRoomService.enterRoom(roomId, userDetails.getUsername());
        model.addAttribute("roomId", roomId);
        model.addAttribute("userId", username);
        return "/chat/roomdetail";
    }

    // 특정 채팅방 정보를 JSON 형태로 반환
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoomDto roomInfo(@PathVariable Long roomId) {
        return chatRoomService.findRoomById(roomId);
    }

    // 특정 채팅방을 나갈 때 호출
    @PostMapping("/room/{roomId}/exit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void exitRoom(@PathVariable Long roomId) {
        chatRoomService.exitRoom(roomId);
    }
}

