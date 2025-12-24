package com.wootae.BoardWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {


    //[권한] ALL
    @GetMapping("/list")
    public String boardListPage(){
        return "boardList";
    }

    //[권한] USER
    @GetMapping("/write")
    public String writePage(){
        return "boardWrite";
    }
}
