package com.example.forum.service;

import com.example.forum.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    private final CommentMapper commentMapper;

    @Autowired
    public CommentService(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }


    // 逻辑删除评论
    public void deleteByPost(Long postId) {
        commentMapper.logicDeleteByPost(postId);
    }

}
