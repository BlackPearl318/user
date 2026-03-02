package com.example.forum.mq.consumer;

import com.example.forum.mapper.PostMapper;
import com.example.forum.mq.post.MQConstants;
import com.example.forum.mq.post.PostCreatedEvent;
import com.example.forum.pojo.Post;
import com.example.forum.search.document.PostDocument;
import com.example.forum.search.repository.PostSearchRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 帖子搜索同步消费者
 *
 * 监听帖子创建事件，
 * 异步构建 ES 索引文档并写入 Elasticsearch。
 */
@Component
public class PostSearchConsumer {

    private final PostMapper postMapper;
    private final PostSearchRepository postSearchRepository;

    @Autowired
    public PostSearchConsumer(PostMapper postMapper,
                              PostSearchRepository postSearchRepository) {
        this.postMapper = postMapper;
        this.postSearchRepository = postSearchRepository;
    }

    @RabbitListener(queues = MQConstants.POST_CREATED_QUEUE)
    public void handlePostCreated(PostCreatedEvent event) {

        Long postId = event.getPostId();

        // 查询完整数据
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return;
        }

        // 构建 ES 文档
        PostDocument document = PostDocument.builder()
                .id(post.getId())
                .title(post.getTitle())
                .summary(post.getSummary())
                .createdAt(post.getCreatedAt())
                .build();

        // 写入 ES
        postSearchRepository.save(document);
    }
}
