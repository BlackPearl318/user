package com.user.controller;

import com.example.common.context.user.UserContext;
import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.ResultUtils;
import com.example.user.dto.request.ReadNotificationRequest;
import com.user.pojo.UserNotification;
import com.user.service.UserNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/not")
public class UserNotificationController {

    private final UserNotificationService notificationService;
    @Autowired
    public UserNotificationController(UserNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 获取某个用户的全部通知
    @GetMapping("/getNotifications")
    public BaseResponse<?> getNotifications(){
        // 获取用户id
        Long userId = Long.valueOf(UserContext.getUserId());

        // 获取到用户的全部通知
        List<UserNotification> userNotifications = notificationService.getNotificationsByUserId(userId);

        return ResultUtils.success(userNotifications);
    }

    // 用户已读某个通知
    @PutMapping("/readNotification")
    public BaseResponse<?> readNotification(@Validated @RequestBody ReadNotificationRequest request){
        // 获取参数
        Long id = request.getId();

        notificationService.changeNotificationStatus(id, true);

        return ResultUtils.success("已标记已读");
    }

    // 用户将通知标记为全部已读
    @PutMapping("/readNotifications")
    public BaseResponse<?> readNotifications(){
        // 获取用户id
        Long userId = Long.valueOf(UserContext.getUserId());
        notificationService.changeNotificationsStatus(userId, true);

        return ResultUtils.success("已全部标记为已读");
    }

    // 用户未读某个通知
    @PutMapping("/unreadNotification")
    public BaseResponse<?> unreadNotification(@Validated @RequestBody ReadNotificationRequest request){
        // 获取参数
        Long id = request.getId();

        notificationService.changeNotificationStatus(id, false);

        return ResultUtils.success("已标记未读");
    }

    // 用户将全部通知标记为未读
    @PutMapping("/unreadNotifications")
    public BaseResponse<?> unreadNotifications(){
        // 获取用户id
        Long userId = Long.valueOf(UserContext.getUserId());

        notificationService.changeNotificationsStatus(userId, false);

        return ResultUtils.success("已全部标记为未读");
    }

}
