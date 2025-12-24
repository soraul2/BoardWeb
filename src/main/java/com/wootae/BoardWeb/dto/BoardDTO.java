package com.wootae.BoardWeb.dto;

import com.wootae.BoardWeb.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardDTO {

    private int num;
    @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다.")
    private String title;

    private String content;
    private String nickname;
    private String date;
    private int count;

}