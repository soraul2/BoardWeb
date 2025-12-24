package com.wootae.BoardWeb.service;

import com.wootae.BoardWeb.dto.JoinDTO;
import com.wootae.BoardWeb.entity.User;
import com.wootae.BoardWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void joinProcess(JoinDTO joinDTO) {

        //같은 아이디가 존재하는가?
        boolean isUser = userRepository.existsByUsername(joinDTO.getUsername());

        if (isUser) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }
        //존재하지 않으면 가입 진행
        User user = new User();

        user.setUsername(joinDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        user.setNickname(joinDTO.getNickname());
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }

}
