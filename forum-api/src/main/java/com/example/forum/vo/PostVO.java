package com.example.forum.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostVO {

    private Long id;
    private String title;
    private String summary;
    private Long userId;
    private Long tenantId;
    private LocalDateTime createdAt;
}
