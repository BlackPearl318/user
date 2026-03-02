package com.example.user.dto.request;


import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * 标记通知为已读请求
 */
@Data
public class ReadNotificationRequest {

    @NotNull(message = "通知ID不能为空")
    @Positive(message = "通知ID不合法")
    private Long id;
}