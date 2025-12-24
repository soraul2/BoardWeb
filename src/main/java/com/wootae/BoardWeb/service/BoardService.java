package com.wootae.BoardWeb.service;

import com.wootae.BoardWeb.dto.BoardDTO;
import com.wootae.BoardWeb.entity.Board;
import com.wootae.BoardWeb.repository.BoardRepository;
import com.wootae.BoardWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    //게시글 리스트 출력
    @Transactional(readOnly = true)
    public Page<BoardDTO> getBoardListProcess(int page,String keyword){

        Pageable pageable = PageRequest.of(page,10,Sort.by(Sort.Direction.DESC,"num"));

        Page<Board> boardPage = null;

        if(keyword == null || keyword.trim().isEmpty()){
            //이미 page에 정렬 정보 있음.
            boardPage = boardRepository.findAll(pageable);
        }else{
            boardPage = boardRepository.findByTitleContaining(keyword,pageable);
        }

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Page<BoardDTO> boardDTOPage = boardPage.map(board -> {
           BoardDTO boardDTO = new BoardDTO();
            boardDTO.setNum(board.getNum());
            boardDTO.setTitle(board.getTitle());
            boardDTO.setNickname(board.getUser().getNickname()); // N+1 주의 (일단은 넘어감)
            boardDTO.setCount(board.getCount());
            boardDTO.setDate(dateTimeFormatter.format(board.getDate()));
            return boardDTO;
        });

        return boardDTOPage;

    }

    //게시글 작성
    @Transactional
    public void writeBoardProcess(BoardDTO boardDTO){

        String title = boardDTO.getTitle();
        String content = boardDTO.getContent();

        if(title == null || content == null){
            throw new IllegalArgumentException("제목이나 내용을 입력하지 않았습니다.");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(username == null){
            throw new IllegalArgumentException("로그인 상태에 문제가 있습니다.");
        }
        //Board에 넣어줘야 하는 것 , User , subject, content , count(0) , 작성일(AUTO) , num(AUTO)

        Board board = new Board();
        board.setTitle(title);
        board.setContent(content);
        board.setUser(userRepository.findByUsername(username));
        boardRepository.save(board);
    }

}
