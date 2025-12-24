package com.wootae.BoardWeb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
//[권한] : USER
@RequestMapping("/user")
public class UserController {

    @GetMapping("/myinfo")
    public String myInfo() {
        return "myInfo"; // templates/myInfo.mustache 호출
    }

}
