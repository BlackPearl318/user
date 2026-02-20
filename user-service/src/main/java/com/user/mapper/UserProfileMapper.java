package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.pojo.UserProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserProfileMapper extends BaseMapper<UserProfile> {

    // 用户第一次创建时生成一个用户信息
    void insertUserProfile(@Param("userId") Long userId, @Param("name") String name);

    // 根据id查询用户的详细信息
    UserProfile selectUserProfileById(@Param("userId") Long userId);

    // 修改用户的信息
    boolean updateUserProfile(@Param("userProfile") UserProfile userProfile);

    // 修改用户的头像信息
    boolean updateUserAvatarProfile(@Param("userId")Long userId, @Param("avatar") String avatar);

    // 获取用户昵称
    String selectUserName(@Param("userId")Long userId);

    // 获取用户头像的uri
    String selectUserAvatar(Long userId);
}
