package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.user.enums.user.UserRoleType;
import com.user.pojo.UserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

    // 根据roleId获取用户权限
    UserRoleType getRole(@Param("roleId") Long roleId);

}
