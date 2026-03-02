package com.example.forum.controller;

import com.example.common.context.tenant.TenantContext;
import com.example.common.context.user.UserContext;
import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.ResultUtils;
import com.example.forum.dto.PostCreateDTO;
import com.example.forum.dto.PostDeleteDTO;
import com.example.forum.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 发布帖子
    @PostMapping("/uploadPost")
    public BaseResponse<?> uploadPost(@Validated @RequestBody PostCreateDTO dto){
        String tenantId = TenantContext.getTenantId();
        String userId = UserContext.getUserId();
        postService.uploadPost(dto, Long.valueOf(tenantId), Long.valueOf(userId));
        return ResultUtils.success("上传成功");
    }

    // 删除帖子
    @DeleteMapping("/deletePost")
    public BaseResponse<?> deletePost(@Validated @RequestBody PostDeleteDTO dto){
        String tenantId = TenantContext.getTenantId();
        String userId = UserContext.getUserId();
        postService.deletePost(dto.getPostId(), Long.valueOf(tenantId), Long.valueOf(userId));
        return ResultUtils.success("删除成功");
    }

    /**
     * 获取所有帖子
     */
    @GetMapping("/listPosts")
    public BaseResponse<?> listPosts(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize) {
        return ResultUtils.success(postService.listAllPosts(pageNum, pageSize));
    }

    /**
     * 根据 tenantId 分页查询帖子
     */
    @GetMapping("/listByTenant")
    public BaseResponse<?> listByTenant(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String tenantId = TenantContext.getTenantId();
        return ResultUtils.success(postService.listPostsByTenantId(Long.valueOf(tenantId), pageNum, pageSize));
    }

    /**
     * 根据 userId 分页查询帖子
     */
    @GetMapping("/listByUser")
    public BaseResponse<?> listByUser(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserContext.getUserId();
        return ResultUtils.success(postService.listPostsByUserId(Long.valueOf(userId), pageNum, pageSize));
    }
}
