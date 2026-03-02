package com.example.forum.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.example.forum.enums.category.CategoryStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tenant_id")
    private Long tenantId;

    private String name;

    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    private CategoryStatus status;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
