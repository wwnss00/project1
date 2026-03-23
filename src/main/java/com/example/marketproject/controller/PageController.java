package com.example.marketproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @GetMapping("/register")
    public String register() { return "auth/register"; }

    @GetMapping("/posts")
    public String posts() { return "post/list"; }

    @GetMapping("/posts/new")
    public String newPost() { return "post/form"; }

    @GetMapping("/posts/{id}")
    public String postDetail() { return "post/detail"; }

    @GetMapping("/posts/edit")
    public String editPost() { return "post/form"; }

    @GetMapping("/chat/rooms")
    public String chatList() { return "chat/list"; }

    @GetMapping("/chat/rooms/{roomId}")
    public String chatRoom() { return "chat/room"; }

    @GetMapping("/mypage")
    public String mypage() { return "user/mypage"; }

    @GetMapping("/mypage/edit")
    public String mypageEdit() { return "user/edit"; }

    @GetMapping("/find-id")
    public String findId() { return "auth/find-id"; }

    @GetMapping("/find-password")
    public String findPassword() { return "auth/find-password"; }
}
