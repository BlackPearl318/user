package com.example.forum.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PostDeleteDTO {
    @NotNull(message = "帖子id不能为空")
    private Long postId;
}
