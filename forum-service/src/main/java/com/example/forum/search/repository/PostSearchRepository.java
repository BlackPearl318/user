package com.example.forum.search.repository;

import com.example.forum.search.document.PostDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Elasticsearch 帖子搜索仓储接口
 *
 * <p>
 * 说明：
 * 1. 该接口用于操作 Elasticsearch 中的帖子索引数据。
 * 2. 仅用于搜索场景，不参与业务事务。
 * 3. 不允许直接在业务逻辑中绕过 MySQL 修改 ES 数据。
 *
 * 数据一致性策略：
 * - 由 MQ 异步消费消息后更新 ES
 * - 禁止直接在 PostService 中双写 MySQL + ES
 *
 * 典型职责：
 * - 保存索引文档
 * - 删除索引文档
 * - 基础查询
 *
 * 高级搜索请使用 ElasticsearchOperations 构建 DSL 查询
 */
public interface PostSearchRepository
        extends ElasticsearchRepository<PostDocument, Long> {

}
