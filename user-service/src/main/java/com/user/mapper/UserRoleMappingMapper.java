package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.pojo.UserRoleMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserRoleMappingMapper extends BaseMapper<UserRoleMapping> {

    // 根据userId获取roleId
    public Long getRoleId(@Param("userId")Long userId);

}
