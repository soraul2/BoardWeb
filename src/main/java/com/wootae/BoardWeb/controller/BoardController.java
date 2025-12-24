package com.wootae.BoardWeb.controller;

import com.wootae.BoardWeb.dto.BoardDTO;
import com.wootae.BoardWeb.entity.Board;
import com.wootae.BoardWeb.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    //[권한] ALL
    @GetMapping("/list")
    public String boardListPage(Model model,@RequestParam(value = "page", defaultValue = "0") int page , @RequestParam(required = false) String keyword){

        Page<BoardDTO> boardList = boardService.getBoardListProcess(page,keyword);

        model.addAttribute("boardList",boardList);
        model.addAttribute("keyword",keyword);

        model.addAttribute("previous", page > 0 ? page - 1 : 0); // 0보다 작아지지 않게
        model.addAttribute("next", page + 1);
        model.addAttribute("current", page + 1);  // 현재 페이지 (사람이 보는 용도라 +1)

        return "boardList";
    }

    //[권한] USER
    @GetMapping("/write")
    public String writePage(){
        return "boardWrite";
    }
    //[권한] USER
    @PostMapping("/write")
    public String writePage(BoardDTO boardDTO, RedirectAttributes redirectAttributes){
        try{
            boardService.writeBoardProcess(boardDTO);
            return "redirect:/board/list";
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("writeBoardError",e.getMessage());
            return "redirect:/board/write";
        }
    }
}
