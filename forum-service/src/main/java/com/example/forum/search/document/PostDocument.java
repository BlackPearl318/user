package com.example.forum.search.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

/**
 * Elasticsearch 索引实体：帖子搜索文档
 *
 * <p>
 * 说明：
 * 1. 该类用于映射 Elasticsearch 中的 post_index 索引结构。
 * 2. 它不是业务领域实体（Domain Entity），也不是数据库实体（MySQL Entity）。
 * 3. 它属于基础设施层（Infrastructure Layer），仅用于搜索场景。
 *
 * 数据来源：
 * - 主数据源为 MySQL 中的 Post 表
 * - 通过 MQ 异步方式同步至 Elasticsearch
 *
 * 一致性说明：
 * - MySQL 为权威数据源（Source of Truth）
 * - Elasticsearch 仅作为搜索索引副本
 * - 不允许直接修改 ES 数据而不更新 MySQL
 *
 * 注意：
 * - 该类结构的变更需要同步调整索引 mapping
 * - 不要将业务逻辑写入该类
 *
 * 索引名称：post_index
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "post_index")
public class PostDocument {

    /**
     * 帖子ID
     * 对应 MySQL 主键
     */
    @Id
    private Long id;

    /**
     * 帖子标题
     * 用于全文检索
     */
    private String title;

    /**
     * 帖子摘要
     * 用于全文检索
     */
    private String summary;

    /**
     * 创建时间
     * 用于排序
     */
    private LocalDateTime createdAt;
}
