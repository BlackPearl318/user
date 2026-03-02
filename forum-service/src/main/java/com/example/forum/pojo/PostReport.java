package com.example.forum.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.example.forum.enums.postreport.ReportStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("post_report")
public class PostReport {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    @TableField("post_id")
    private Long postId;

    @TableField("reporter_id")
    private Long reporterId;

    private String reason;

    private ReportStatus status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
