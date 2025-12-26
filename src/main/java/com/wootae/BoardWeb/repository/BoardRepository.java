package com.wootae.BoardWeb.repository;

import com.wootae.BoardWeb.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    Page<Board> findAllByOrderByNumDesc(Pageable pageable);

    Page<Board> findByTitleContaining(String keyword,Pageable pageable);

    Board findByNum(int num);

}
