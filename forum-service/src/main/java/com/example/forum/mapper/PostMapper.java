package com.example.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.pojo.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    /**
     * 查询所有帖子
     */
    List<Post> selectAllPosts();
    /**
     * 根据 tenantId 查询帖子
     */
    List<Post> selectPostsByTenantId(@Param("tenantId") Long tenantId);
    /**
     * 根据 userId 查询帖子
     */
    List<Post> selectPostsByUserId(@Param("userId") Long userId);


    // 逻辑删除帖子
    int logicDelete(@Param("postId") Long postId);
}
