package com.wootae.BoardWeb.repository;

import com.wootae.BoardWeb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {

    //같은 username 이 있는 경우
    boolean existsByUsername(String username);

}
