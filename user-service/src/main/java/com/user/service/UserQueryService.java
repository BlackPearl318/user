package com.user.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.user.dto.UserDTO;
import com.example.user.enums.user.UserRoleType;
import com.user.mapper.UserMapper;
import com.user.pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserQueryService {

    private final UserMapper userMapper;

    private final UserRoleService userRoleService;

    @Autowired
    public UserQueryService(UserMapper userMapper, UserRoleService userRoleService) {
        this.userMapper = userMapper;
        this.userRoleService = userRoleService;
    }

    // 查询该租户下的用户数量
    public Integer getUserCount(Long tenantId){
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userMapper.countByTenantId(tenantId);
    }

    // 获取当前租户下的所有用户
    public List<UserDTO> getUsers(Long tenantId, Long userId){
        // 鉴权
        UserRoleType userRole = userRoleService.getUserRole(userId);
        if(userRole.equals(UserRoleType.USER)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        Long currentTenantId = userMapper.getTenantId(userId);
        if(!(currentTenantId.equals(tenantId))){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 查询
        List<User> users = userMapper.getUsers(tenantId);

        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserDTO> list = new ArrayList<>(users.size());

        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            list.add(userDTO);
        }

        return list;
    }

    // 租户目前用户数量
    public Integer countByTenantId(Long tenantId){
        return userMapper.countByTenantId(tenantId);
    }
}
