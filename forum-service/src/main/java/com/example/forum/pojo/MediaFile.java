package com.example.forum.pojo;

import com.example.forum.enums.mediafile.MediaFileType;
import com.example.forum.enums.mediafile.MediaStatus;
import com.example.forum.enums.mediafile.RefType;
import lombok.Data;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 媒体文件实体类
 * 对应数据库表：media_file
 */
@Data
@TableName("media_file")
public class MediaFile {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 上传用户ID
     */
    private Long userId;

    /**
     * 关联类型：post / comment
     */
    private RefType refType;

    /**
     * 关联业务ID
     */
    private Long refId;

    /**
     * 文件访问地址
     */
    private String fileUrl;

    /**
     * 文件类型：image / video
     */
    private MediaFileType fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 状态：0正常 1删除 2违规
     */
    private MediaStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}