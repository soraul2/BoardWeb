package com.wootae.BoardWeb.controller;

import com.wootae.BoardWeb.dto.JoinDTO;
import com.wootae.BoardWeb.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class JoinController {

    private final JoinService joinService;

    //1. 페이지 이동
    @GetMapping("/join")
    public String joinPage() {
        return "join";
    }

    //2. 실제 저장
    @PostMapping("/join")
    public String joinController(JoinDTO joinDTO, Model model) {
        try {
            joinService.joinProcess(joinDTO);
            return "login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("JoinErrorMessage", e.getMessage());
            return "join";
        }
    }

}
