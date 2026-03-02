package com.example.forum.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.pojo.MediaFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MediaFileMapper extends BaseMapper<MediaFile> {

    // 绑定附件到帖子
    void batchBindToPost(@Param("ids") List<Long> ids, @Param("postId") Long postId);

    // 解绑附件和帖子
    void unbindByPost(@Param("postId") Long postId);

}
