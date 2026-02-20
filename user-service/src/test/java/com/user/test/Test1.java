package com.user.test;

import com.user.mapper.UserMapper;
import com.user.mapper.UserRoleMapper;
import com.user.mapper.UserRoleMappingMapper;
import com.user.service.TenantService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class Test1 {

    @Resource
    private UserRoleMappingMapper userRoleMappingMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;


    @Resource
    private TenantService tenantService;


    @Test
    public void test2()  {

        tenantService.getTenants().forEach(System.out::println);

    }

}
