package com.user.controller;

import com.example.common.context.user.UserContext;
import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.ResultUtils;
import com.example.user.dto.UserProfileDTO;
import com.example.user.dto.request.ResetProfileRequest;
import com.user.pojo.UserProfile;
import com.user.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/prof")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // 上传头像
    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> avatar(@RequestPart("file") MultipartFile file) {

        String userId = UserContext.getUserId();
        // 调用业务层
        userProfileService.uploadAvatar(file, Long.valueOf(userId));

        return ResultUtils.success("上传头像成功");
    }

    // 获取用户个人资料
    @GetMapping("/getProfile")
    public BaseResponse<?> getProfile(){
        // 获取用户id
        String userId = UserContext.getUserId();

        // 获取用户的个人资料
        UserProfileDTO userProfile = userProfileService.getProfile(Long.valueOf(userId));

        return ResultUtils.success(userProfile);
    }

    // 用户编辑个人信息
    @PutMapping("/editProfile")
    public BaseResponse<?> editProfile(@Validated @RequestBody ResetProfileRequest request){

        // 构建用户个人资料
        UserProfile userProfile = new UserProfile();

        // 获取用户id
        String userId = UserContext.getUserId();

        // 将获取的传输数据设置到userProfile
        userProfile.setUserId(Long.valueOf(userId));
        userProfile.setName(request.getName());
        userProfile.setGender(request.getGender());
        userProfile.setDateOfBirth(request.getDateOfBirth());
        userProfile.setBiography(request.getBiography());

        // 修改用户信息
        userProfileService.changeProfile(userProfile);
        return ResultUtils.success("修改信息成功");
    }

}
