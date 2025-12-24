package com.wootae.BoardWeb.service;

import com.wootae.BoardWeb.dto.CustomUserDetails;
import com.wootae.BoardWeb.entity.User;
import com.wootae.BoardWeb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("잘못된 계정입니다.");
        }

        return new CustomUserDetails(user);
    }

}
