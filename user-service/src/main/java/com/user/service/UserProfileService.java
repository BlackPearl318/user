package com.user.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.user.mapper.UserProfileMapper;
import com.user.pojo.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
public class UserProfileService {

    @Value("${avatar.dir}")
    private String AVATAR_DIR;

    @Value("${avatar.url.prefix}")
    private String AVATAR_URL_PREFIX;

    @Value("#{'${avatar.ext}'.split(',')}")
    private Set<String> ALLOWED_EXT;

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class); // 日志

    private final UserProfileMapper userProfileMapper;

    @Autowired
    public UserProfileService(UserProfileMapper userProfileMapper) {
        this.userProfileMapper = userProfileMapper;
    }

    // 插入用户的个人资料
    @Transactional
    public void insertProfile(Long userId, String name){
        try{
            userProfileMapper.insertUserProfile(userId, name);
        }catch (DataAccessException e){
            logger.error("用户信息记录插入失败，userId={}", userId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户信息记录插入失败");
        }
    }

    // 查看用户个人信息
    public UserProfile getProfile(Long userId) {

        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_INVALID, "用户ID非法");
        }

        UserProfile userProfile = userProfileMapper.selectUserProfileById(userId);

        if (userProfile == null) {
            logger.warn("用户个人信息不存在, userId={}", userId);
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "用户信息不存在");
        }

        return userProfile;
    }

    // 用户编辑个人信息,头像除外
    @Transactional
    public void changeProfile(UserProfile userProfile) {

        if (userProfile == null) {
            throw new BusinessException(ErrorCode.PARAMS_INVALID, "参数异常");
        }

        boolean rows;
        try {
            rows = userProfileMapper.updateUserProfile(userProfile);
        } catch (DataAccessException e) {
            logger.error("更新用户资料失败, profile={}", userProfile, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }

        if (!rows) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新失败，数据未变更");
        }
    }

    // 用户上传头像
    @Transactional
    public void uploadAvatar(MultipartFile file, Long userId) {
        // 1. 校验文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.PARAMS_INVALID);
        }

        // 2. 校验后缀
        String ext = StringUtils.getFilenameExtension(originalFilename);
        if (ext == null || !ALLOWED_EXT.contains(ext.toLowerCase())) {
            throw new BusinessException(ErrorCode.PARAMS_INVALID, "不支持的图片格式");
        }

        // 3. 校验是否为真实图片（防止伪造）
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                throw new BusinessException(ErrorCode.PARAMS_INVALID, "非法图片文件");
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "读取图片失败");
        }

        // 4. 构建文件名
        String filename = "userId_" + userId + "_avatar." + ext.toLowerCase();

        // 5. 确保目录存在
        Path avatarPath = Paths.get(AVATAR_DIR).toAbsolutePath().normalize();
        try {
            Files.createDirectories(avatarPath);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建头像目录失败");
        }

        // 6. 保存文件（覆盖旧头像）
        Path targetPath = avatarPath.resolve(filename);
        try {
            file.transferTo(targetPath.toFile());
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存头像失败");
        }

        // 7. 构建访问 URL
        String avatarUrl = AVATAR_URL_PREFIX + filename;

        // 8. 更新数据库
        try {
            userProfileMapper.updateUserAvatarProfile(userId, avatarUrl);
        } catch (DataAccessException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新头像信息失败");
        }
    }
}
