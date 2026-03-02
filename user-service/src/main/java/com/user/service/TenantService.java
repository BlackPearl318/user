package com.user.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.sms.feign.SmsClient;
import com.example.user.dto.TenantDTO;
import com.example.user.dto.TenantInfoDTO;
import com.example.user.enums.tenant.TenantPlan;
import com.example.user.enums.tenant.TenantStatus;
import com.example.user.enums.user.UserRoleType;
import com.example.user.mq.tenant.MQConstants;
import com.example.user.mq.tenant.TenantCreatedEvent;
import com.user.mapper.TenantMapper;
import com.user.pojo.Tenant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class TenantService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String PREFIX = "t";   // 避免纯数字子域名

    private static final Logger logger = LoggerFactory.getLogger(TenantService.class); // 日志

    private final TenantMapper tenantMapper;

    private final UserService userService;
    private final UserQueryService userQueryService;

    private final SmsClient smsClient;

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public TenantService(TenantMapper tenantMapper,
                         UserService userService,
                         UserQueryService userQueryService,
                         SmsClient smsClient,
                         RabbitTemplate rabbitTemplate) {
        this.tenantMapper = tenantMapper;
        this.userService = userService;
        this.userQueryService = userQueryService;
        this.smsClient = smsClient;
        this.rabbitTemplate = rabbitTemplate;
    }


    // 获取所有的租户code和id
    public List<TenantDTO> getTenants(){
        return tenantMapper.selectList(null)
                .stream()
                .map(t -> {
                    TenantDTO dto = new TenantDTO();
                    dto.setId(t.getId());
                    dto.setCode(t.getCode());
                    return dto;
                })
                .toList();
    }

    /**
     * 租户注册
     * @param tName 租户的公司名
     * @param phone 手机号
     * @param password 密码
     * @return 子域名
     */
    @Transactional
    public String doRegister(String tName, String phone, String password) {

        Tenant tenant = new Tenant();
        String tCode = generateCode();

        tenant.setName(tName);
        tenant.setCode(tCode);
        tenant.setStatus(TenantStatus.NOT_ACTIVATE);

        try {

            // 插入租户
            tenantMapper.insert(tenant);

            // 注册用户
            userService.doRegister(
                    tenant.getId(),
                    phone,
                    password,
                    UserRoleType.TENANT
            );

            Long tenantId = tenant.getId();

            // 事务提交后发送 MQ
            if (TransactionSynchronizationManager.isActualTransactionActive()) {

                TransactionSynchronizationManager.registerSynchronization(
                        new TransactionSynchronization() {
                            @Override
                            public void afterCommit() {

                                TenantCreatedEvent event =
                                        new TenantCreatedEvent(
                                                tenantId,
                                                tCode,
                                                tName
                                        );

                                rabbitTemplate.convertAndSend(
                                        MQConstants.USER_EXCHANGE,
                                        MQConstants.TENANT_CREATED_KEY,
                                        event
                                );
                            }
                        }
                );
            }

            return tCode;

        } catch (DuplicateKeyException e) {
            throw new BusinessException(
                    ErrorCode.STATUS_CONFLICT,
                    "租户信息或手机号已存在"
            );
        } catch (Exception e) {
            logger.error("租户注册失败，phone={}", phone, e);
            throw new BusinessException(
                    ErrorCode.SYSTEM_ERROR,
                    "租户初始化失败"
            );
        }
    }
    /**
     * 生成唯一子域名 tenantCode
     * 格式示例：t-lp4f8x-k3a
     */
    private String generateCode() {
        long timestamp = System.currentTimeMillis();

        // 时间戳转 base36，缩短长度
        String timePart = Long.toString(timestamp, 36);

        // 3位随机串
        String randomPart = randomBase36(3);

        return String.format("%s-%s-%s", PREFIX, timePart, randomPart);
    }
    private String randomBase36(int len) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars[RANDOM.nextInt(chars.length)]);
        }
        return sb.toString();
    }


    // 设置租户套餐类型
    @Transactional
    public void setTenantPlan(Long tenantId, TenantPlan plan) {

        Tenant tenant = tenantMapper.selectById(tenantId);
        if (tenant == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "租户不存在");
        }

        // 1. 检测用户数：如果新套餐的用户上限小于当前实际用户数，禁止降级
        int currentUsers = userQueryService.getUserCount(tenantId);
        if (currentUsers > plan.getMaxUsers()) {
            throw new BusinessException(
                    ErrorCode.OPERATION_ERROR,
                    String.format("当前用户数(%d)已超过新套餐限制(%d)，请先移除多余用户", currentUsers, plan.getMaxUsers())
            );
        }

//        long currentStorage = fileQueryService.getTenantStorageUsage(tenantId);
//
//        if (currentStorage > plan.getMaxStorageMb()) {
//            throw new BusinessException(
//                    ErrorCode.OPERATION_ERROR,
//                    String.format("当前存储使用(%dMB)已超过新套餐限制(%dMB)，请先清理数据",
//                            currentStorage,
//                            plan.getMaxStorageMb())
//            );
//        }

        tenant.setPlanType(plan.getCode());
        tenant.setMaxUsers(plan.getMaxUsers());
        tenant.setMaxStorageMb(plan.getMaxStorageMb());
        tenant.setAllowRegister(plan.isAllowRegister());

        // 计算到期时间
        if (!plan.getValidDuration().isZero()) {
            tenant.setExpireTime(Timestamp.from(Instant.now().plus(plan.getValidDuration())));
        } else {
            tenant.setExpireTime(null); // 永久有效
        }

        tenantMapper.updateById(tenant);
    }

    // 获取当前租户的信息
    public TenantInfoDTO getTenantInfo(Long tenantId) {

        Tenant tenant = tenantMapper.selectById(tenantId);

        if (tenant == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "租户不存在");
        }

        TenantInfoDTO dto = new TenantInfoDTO();
        BeanUtils.copyProperties(tenant, dto);

        return dto;
    }




















}
