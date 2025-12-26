package com.wootae.BoardWeb.controller;

import com.wootae.BoardWeb.dto.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;

@ControllerAdvice
public class GlobalModelConfig {

    //@ModelAttributes 모든 컨트롤러가 실행되기 전에 이 메서드가 먼저 실행됨
    @ModelAttribute
    public void addAttributes(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //로그인 한 상태인지 확인
        if(auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())){
            //principal 을 CustomUserDetails 로 변환
            Object principal = auth.getPrincipal();
            if(principal instanceof CustomUserDetails){
                CustomUserDetails customUserDetails = (CustomUserDetails) principal;

                //mustache 에서 쓸 변수명 등록
                model.addAttribute("username",customUserDetails.getUsername());

                Collection<? extends GrantedAuthority> collection = customUserDetails.getAuthorities();
                Iterator<? extends GrantedAuthority> iterator = collection.iterator();
                GrantedAuthority grantedAuthority = iterator.next();
                String role = grantedAuthority.getAuthority();
                String nickname = customUserDetails.getNickname();

                model.addAttribute("role",role);
                model.addAttribute("isLogin",true);
                model.addAttribute("nickname",nickname);
            }else{
                model.addAttribute("isLogin",false);
            }


        }

    }

}
