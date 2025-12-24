package com.wootae.BoardWeb.repository;

import com.wootae.BoardWeb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    //[회원가입]
    boolean existsByUsername(String username);

    //[로그인]
    User findByUsername(String username);

}
