package com.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.pojo.UserNotification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserNotificationMapper extends BaseMapper<UserNotification> {

    //新增通知
    boolean addUserNotification(@Param("userNotification") UserNotification userNotification);

    //修改通知是否为已读状态
    boolean updateUserNotificationStatus(@Param("id") Long id, @Param("isRead") boolean isRead);

    //修改某个用户的全部通知是否为已读状态
    boolean updateUserNotificationsStatus(@Param("userId") Long userId,@Param("isRead") boolean isRead);

    //查询某个用户的全部通知
    List<UserNotification> selectUserNotificationsByUserId(@Param("userId") Long userId);












}
