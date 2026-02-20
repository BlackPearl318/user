package com.user.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.user.dto.UserDTO;
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

    @Autowired
    public UserQueryService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    // 查询该租户下的用户数量
    public Integer getUserCount(Long tenantId){
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userMapper.countByTenantId(tenantId);
    }

    // 获取当前租户下的所有用户
    public List<UserDTO> getUsers(Long tenantId){

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
}
