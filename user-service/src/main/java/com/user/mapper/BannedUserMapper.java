package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.pojo.BannedUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BannedUserMapper extends BaseMapper<BannedUser> {

    // 添加被禁用的用户
    boolean insertBannedUser(@Param("bannedUser") BannedUser bannedUser);

}
