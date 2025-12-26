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

    @Transactional
    public void editProcess(BoardDTO boardDTO,String username , int num){
        Board board = boardRepository.findById(num)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if(board.getUser().getUsername().equals(username)){
            board.setTitle(boardDTO.getTitle());
            board.setContent(boardDTO.getContent());
        }else{
            throw new IllegalArgumentException("수정할 권한이 없습니다.");
        }
        return;
    }

    //게시글 업데이트 이동 [권한] 해당 게시글 만든 유저
    @Transactional(readOnly = true)
    public BoardDTO edtiBoardPage(String username , int num){
        Board board = boardRepository.findById(num)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        BoardDTO boardDTO = new BoardDTO();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if(board.getUser().getUsername().equals(username)){
            boardDTO.setNum(board.getNum());
            boardDTO.setContent(board.getContent());
            boardDTO.setDate(dtf.format(board.getDate()));
            boardDTO.setCount(board.getCount());
            boardDTO.setNickname(board.getUser().getNickname());
            boardDTO.setTitle(board.getTitle());
        }else{
            throw new IllegalArgumentException("수정할 권한이 없습니다.");
        }
        return boardDTO;
    }

    //게시글 삭제 [권한] 게시글을 작성한 USER
    @Transactional
    public void deleteBoard(int num,String username,String role){
        Board board = boardRepository.findById(num)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        if(board.getUser().getUsername().equals(username) || role.equals("ROLE_ADMIN")){
            boardRepository.delete(board);
        }else{
            throw new IllegalArgumentException("삭제할 권한이 없습니다.");
        }
    }

    //게시글 상세 보기
    @Transactional
    public BoardDTO getBoardPage(int num){
        Board board = boardRepository.findByNum(num);

        //자동으로 +1 저장 해줌
        board.setCount(board.getCount()+1);

        if(board == null){
            throw new IllegalArgumentException("존재하지 않는 게시글 입니다.");
        }

        BoardDTO boardDTO = new BoardDTO();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        boardDTO.setNum(board.getNum());
        boardDTO.setContent(board.getContent());
        boardDTO.setDate(dtf.format(board.getDate()));
        boardDTO.setCount(board.getCount());
        boardDTO.setNickname(board.getUser().getNickname());
        boardDTO.setTitle(board.getTitle());

        return boardDTO;
    }

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
