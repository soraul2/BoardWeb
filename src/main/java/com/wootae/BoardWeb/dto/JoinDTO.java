package com.wootae.BoardWeb.dto;

import lombok.Data;

@Data
public class JoinDTO {

    //id Integer로 자동 추가
    private String username;
    private String password;
    private String nickname;

}
