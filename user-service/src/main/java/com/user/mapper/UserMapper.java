package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /*
    普通用户

     */

    // 查询用户id
    Long selectIdByPhoneAndTenantId(@Param("phone") String phone, @Param("tenantId") Long tenantId);
    Long selectIdByCredentials(@Param("username") String username, @Param("password") String password);
    Long selectIdByUsername(@Param("username") String username);


    //查询用户手机号
    String selectUserPhone(@Param("id") Long userId);


    // 查询用户哈希密码
    String selectPasswordByUsername(@Param("username")String username);
    String selectPasswordByPhoneAndTenantId(@Param("phone")String phone, @Param("tenantId") Long tenantId);
    String selectPasswordById(@Param("userId")Long userId);
    // 更新用户密码
    boolean updateUserPassword(@Param("id") Long userId, @Param("password") String newPassword);



    // 改变用户状态
    boolean changeUserStatus(@Param("id") Long userId, @Param("status")Integer status);
    // 查询用户状态
    Integer selectUserStatus(@Param("id") Long userId);




    // 绑定手机号
    boolean bindPhone(@Param("id") Long userId, @Param("phone") String phone);

    // 绑定邮箱
    boolean bindEmail(@Param("id") Long userId, @Param("email") String email);


    // 检查手机号是否存在于目标租户中
    boolean existsByPhone(@Param("phone") String phone, @Param("tenantId") Long tenantId);
    // 检查手机号是否存在于数据库中(全局唯一)
    boolean existsByPhoneGlobal(@Param("phone") String phone);

    // 检查邮箱是否存在于目标租户中
    boolean existsByEmail(@Param("email") String email, @Param("tenantId") Long tenantId);




    // 冻结用户
    int freezeUser(@Param("userId") Long userId, @Param("frozenUntil") Timestamp frozenUntil);
    // 解冻用户
    int unfreeze(@Param("userId") Long userId);
    // 查询冻结时间
    Timestamp selectFrozenUntil(@Param("userId") Long userId);

    // 统计该租户名下的用户数量
    Integer countByTenantId(@Param("tenantId")Long tenantId);

    // 获取该租户名下的所有用户
    List<User> getUsers(@Param("tenantId")Long tenantId);
}
