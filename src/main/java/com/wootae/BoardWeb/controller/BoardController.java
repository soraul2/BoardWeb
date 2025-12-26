package com.wootae.BoardWeb.controller;

import com.wootae.BoardWeb.dto.BoardDTO;
import com.wootae.BoardWeb.entity.Board;
import com.wootae.BoardWeb.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
@Controller
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    //게시글 수정
    @PostMapping("/edit")
    public String editController(BoardDTO boardDTO,@RequestParam int num,RedirectAttributes redirectAttributes){
            //1. 해당 게시글 주인이 아닌 경우 (비회원 , 다른 아이디)
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //현재 계정 아이디
            String username = auth.getName();

            try{
                boardService.editProcess(boardDTO,username,num);
                return "redirect:/board/detail/"+num;
            }catch (IllegalArgumentException e){
                redirectAttributes.addFlashAttribute("error",e.getMessage());
                return "redirect:/board/detail/"+num;
            }
        }

    //게시글 수정 페이지 이동 [권한] USER
    @GetMapping("/edit/{num}")
    public String editPage(@PathVariable Integer num, Model model,RedirectAttributes redirectAttributes){
        //1. 해당 게시글 주인이 아닌 경우 (비회원 , 다른 아이디)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //현재 계정 아이디
        String username = auth.getName();

        try{
            BoardDTO board = boardService.edtiBoardPage(username,num);
            model.addAttribute("board",board);
            return "edit";
        }catch (IllegalArgumentException e){
            redirectAttributes.addFlashAttribute("error",e.getMessage());
            return "redirect:/board/list";
        }

    }

    //[권한] 게시글 작성한 USER
    @PostMapping("/delete")
    public String deletePage(Model model, HttpServletRequest request,RedirectAttributes redirectAttributes) {
        String num = request.getParameter("num");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Collection<? extends GrantedAuthority> collection = auth.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = collection.iterator();
        GrantedAuthority grantedAuthority = iterator.next();
        String role = grantedAuthority.getAuthority();

        try {
            boardService.deleteBoard(Integer.parseInt(num), username, role);
            return "redirect:/board/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error",e.getMessage());
            return "redirect:/board/detail/" + Integer.parseInt(num);
        }
    }

    //[권한] ALL
    @GetMapping("/detail/{num}")
    public String boardDetailPage(@PathVariable Integer num, Model model, RedirectAttributes redirectAttributes) {

        try {
            BoardDTO boardDTO = boardService.getBoardPage(num);
            model.addAttribute("board", boardDTO);
            return "detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/board/list";
        }

    }

    //[권한] ALL
    @GetMapping("/list")
    public String boardListPage(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(required = false) String keyword) {

        Page<BoardDTO> boardList = boardService.getBoardListProcess(page, keyword);

        model.addAttribute("boardList", boardList);
        model.addAttribute("keyword", keyword);

        model.addAttribute("previous", page > 0 ? page - 1 : 0); // 0보다 작아지지 않게
        model.addAttribute("next", page + 1);
        model.addAttribute("current", page + 1);  // 현재 페이지 (사람이 보는 용도라 +1)

        return "boardList";
    }

    //[권한] USER
    @GetMapping("/write")
    public String writePage() {
        return "boardWrite";
    }

    //[권한] USER
    @PostMapping("/write")
    public String writePage(BoardDTO boardDTO, RedirectAttributes redirectAttributes) {
        try {
            boardService.writeBoardProcess(boardDTO);
            return "redirect:/board/list";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/board/write";
        }
    }
}
