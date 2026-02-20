package com.user.service;

import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.sms.dto.SmsRequest;
import com.example.sms.feign.SmsClient;
import com.example.user.dto.TenantDTO;
import com.example.user.dto.TenantInfoDTO;
import com.example.user.enums.TenantPlan;
import com.example.user.enums.TenantStatus;
import com.example.user.enums.UserRoleType;
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

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

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
                         UserQueryService userQueryService, SmsClient smsClient,
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
     * @param password1 密码
     * @param password2 确认密码
     * @param code 验证码
     * @return 子域名
     */
    public String register(String tName, String phone, String password1, String password2, String code) {

        // 1. 基础参数校验 (与用户注册类似)
        if (phone == null || password1 == null || password2 == null || code == null) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }
        if (tName == null || tName.equals("")) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }

        // 2. 手机号格式校验
        if (!userService.isValidPhone(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }

        // 3. 检查手机号是否已被占用（全局校验）
        // 租户注册不允许手机号在全系统内重复
        if (userService.existsByPhoneGlobal(phone)) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT, "该手机号已注册，请直接登录或更换手机号");
        }

        // 4. 密码一致性校验
        if (!Objects.equals(password1, password2)) {
            throw new BusinessException(ErrorCode.DATA_ACCESS_ERROR, "两次密码输入不一致");
        }

        // 5. 验证短信验证码（远程调用）
        BaseResponse<?> smsResult;
        try {
            smsResult = smsClient.verifySms(new SmsRequest(phone, code));
        } catch (Exception e) {
            logger.error("调用 sms-service 校验验证码失败，phone={}", phone, e);
            throw new BusinessException(ErrorCode.DEPENDENCY_ERROR);
        }

        if (smsResult.getCode() != 0) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        // 6. 进入事务，执行租户的初始化
        return doRegister(tName, phone, password1);
    }
    @Transactional
    protected String doRegister(String tName, String phone, String password) {

        Tenant tenant = new Tenant();
        String tCode = generateCode();

        tenant.setName(tName);
        tenant.setCode(tCode);
        tenant.setStatus(TenantStatus.NOT_ACTIVATE.getCode());

        try {

            // 插入租户信息
            tenantMapper.insert(tenant);

            // 注册用户信息
            userService.doRegister(tenant.getId(), phone, password, UserRoleType.TENANT);

            // 发布 MQ 事件
            TenantCreatedEvent event = new TenantCreatedEvent(
                    tenant.getId(),
                    tCode,
                    tName
            );
            rabbitTemplate.convertAndSend(
                    MQConstants.USER_EXCHANGE,
                    MQConstants.TENANT_CREATED_KEY,
                    event
            );

            return tCode;

        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT, "租户信息或手机号已存在");
        } catch (Exception e) {
            logger.error("租户注册失败，phone={}", phone, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "租户初始化失败");
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
