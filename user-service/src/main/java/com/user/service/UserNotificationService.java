package com.user.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.user.enums.notification.NotificationType;
import com.user.mapper.UserNotificationMapper;
import com.user.pojo.UserNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(UserNotificationService.class); // 日志

    private final UserNotificationMapper userNotificationMapper;

    @Autowired
    public UserNotificationService(UserNotificationMapper userNotificationMapper) {
        this.userNotificationMapper = userNotificationMapper;
    }

    //发送用户通知
    @Transactional
    public void sendNotification(Long userId, String info, NotificationType type){

        UserNotification userNotification = new UserNotification();
        userNotification.setUserId(userId);
        userNotification.setMessage(info);
        userNotification.setRead(false);
        userNotification.setType(type);

        try{
            userNotificationMapper.addUserNotification(userNotification);
        }catch (DataAccessException e){
            logger.error("插入用户通知记录时出错，用户id：{}", userId, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    // 更改某个通知的状态
    @Transactional
    public void changeNotificationStatus(Long id, boolean isRead){
        try{
            userNotificationMapper.updateUserNotificationStatus(id, isRead);
        }catch (DataAccessException e){
            logger.error("更改通知记录时出错，通知id：{}", id, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    // 更改某个用户的全部通知的状态
    @Transactional
    public void changeNotificationsStatus(Long userId, boolean isRead){
        try{
            userNotificationMapper.updateUserNotificationsStatus(userId,isRead);
        }catch (DataAccessException e){
            logger.error("更改用户通知记录时出错，用户id：{}", userId, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
    }

    //查询某个用户的全部通知
    public List<UserNotification> getNotificationsByUserId(Long userId){
        return userNotificationMapper.selectUserNotificationsByUserId(userId);
    }
}
