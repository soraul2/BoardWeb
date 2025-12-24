package com.wootae.BoardWeb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Board {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int num;
    @Column(length = 120, nullable = false)
    private String title;
    @Column(length = 2000, nullable = false)
    private String content;
    //속도 개선 [지연 방식]
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private int count = 0;
    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime date;

}
