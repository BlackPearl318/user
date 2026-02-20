package com.user.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.user.enums.UserRoleType;
import com.user.mapper.UserRoleMapper;
import com.user.mapper.UserRoleMappingMapper;
import com.user.pojo.UserRoleMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleService.class); // 日志

    private final UserRoleMapper userRoleMapper;
    private final UserRoleMappingMapper userRoleMappingMapper;

    @Autowired
    public UserRoleService(UserRoleMapper userRoleMapper, UserRoleMappingMapper userRoleMappingMapper) {
        this.userRoleMapper = userRoleMapper;
        this.userRoleMappingMapper = userRoleMappingMapper;
    }

    // 获取用户权限
    public UserRoleType getUserRole(Long userId) {
        // 参数验证
        if (userId == null || userId < 0) {
            logger.warn("用户ID为null");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        try {
            // 1. 获取角色映射
            Long roleId = userRoleMappingMapper.getRoleId(userId);
            if (roleId == null) {
                logger.info("用户 {} 未分配角色", userId);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }

            // 2. 获取角色代码
            Long roleCode = userRoleMapper.getRole(roleId);
            if (roleCode == null) {
                logger.warn("角色ID {} 对应的角色代码不存在，用户: {}", roleId, userId);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }

            // 3. 安全类型转换
            int intRoleCode;
            try {
                intRoleCode = Math.toIntExact(roleCode);
            } catch (ArithmeticException e) {
                logger.error("角色代码溢出，用户: {}, 原始值: {}", userId, roleCode, e);
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

            // 4. 转换为枚举类型
            UserRoleType roleType = UserRoleType.fromCode(intRoleCode);
            if (roleType == null) {
                logger.warn("未知的角色代码: {}, 用户: {}", intRoleCode, userId);
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }

            logger.debug("用户 {} 的角色: {}", userId, roleType);
            return roleType;

        } catch (Exception e) {
            logger.error("获取用户角色失败，用户ID: {}", userId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }


    // 设置用户权限
    public void setUserRole(Long userId, UserRoleType role) {
        // 参数验证
        if (userId == null || userId < 0) {
            logger.warn("用户ID为null");
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserRoleMapping userRoleMapping = new UserRoleMapping();
        userRoleMapping.setUserId(userId);
        userRoleMapping.setRoleId((long) role.getCode());

        try {
            userRoleMappingMapper.insert(userRoleMapping);
        } catch (DataAccessException e) {
            logger.error("插入用户角色权限映射失败，用户ID: {}", userId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
}
