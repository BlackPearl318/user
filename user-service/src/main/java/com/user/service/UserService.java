package com.user.service;

import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.sms.dto.SmsRequest;
import com.example.sms.feign.SmsClient;
import com.example.user.dto.UserDTO;
import com.example.user.enums.*;
import com.user.mapper.*;
import com.user.pojo.*;
import com.user.utils.PasswordUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final Pattern PHONE_PATTERN = Pattern.compile("^[1][3-9][0-9]{9}$"); // 适用于中国大陆手机号格式
    private final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"); // 邮箱格式

    private static final Logger logger = LoggerFactory.getLogger(UserService.class); // 日志

    private final UserMapper userMapper;

    private final UserNotificationService userNotificationService;
    private final UserProfileService userProfileService;
    private final UserRoleService userRoleService;

    private final SmsClient smsClient;

    @Autowired
    public UserService(UserMapper userMapper,
                       UserNotificationService userNotificationService,
                       UserProfileService userProfileService,
                        UserRoleService userRoleService,
                       SmsClient smsClient) {
        this.userMapper = userMapper;
        this.userNotificationService = userNotificationService;
        this.userProfileService = userProfileService;
        this.userRoleService = userRoleService;
        this.smsClient = smsClient;
    }


    /**
     * 普通用户注册
     * @param tenantId 当下所处的租户
     * @param phone 注册用的手机号
     * @param password1 密码
     * @param password2 二次确认的密码
     * @param code 收到的验证码
     */
    public void register(Long tenantId, String phone, String password1, String password2, String code) {

        // 1. 基础参数校验
        if (phone == null || password1 == null || password2 == null || code == null) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }
        if (tenantId == null || tenantId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }

        // 2. 手机号格式校验
        if (!this.isValidPhone(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }

        // 3.检查是否已经注册
        if(this.existsByPhone(phone, tenantId)){
            throw new BusinessException(ErrorCode.STATUS_CONFLICT, "该手机号已被注册");
        }

        // 4. 密码一致性校验（本地校验，避免无意义远程调用）
        if (!Objects.equals(password1, password2)) {
            throw new BusinessException(ErrorCode.DATA_ACCESS_ERROR, "两次密码输入不一致");
        }

        // 5. 验证短信验证码（事务外，远程调用）
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

        // 6. 进入事务，执行真正注册
        doRegister(tenantId, phone, password1, UserRoleType.USER);
    }
    @Transactional
    protected void doRegister(Long tenantId, String phone, String plainPassword, UserRoleType role) {

        // 1. 生成用户数据
        User user = new User();
        user.setUsername(this.generateUniqueUsername());
        user.setPassword(PasswordUtils.hash(plainPassword));
        user.setPhone(phone);
        user.setStatus(UserStatus.WARNING.getCode());
        user.setTenantId(tenantId);

        String nickname = this.generateName();

        try {
            // 2. 插入用户（数据库唯一索引保证并发安全）
            userMapper.insert(user);

            // 3. 初始化用户资料
            userProfileService.insertProfile(user.getId(), nickname);

            // 4. 配置用户权限
            userRoleService.setUserRole(user.getId(), role);

        } catch (DuplicateKeyException e) {
            // 并发注册同一手机号
            throw new BusinessException(ErrorCode.STATUS_CONFLICT, "并发异常");

        } catch (DataAccessException e) {
            logger.error("注册用户失败，phone={}", phone, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }
    // 生成一个默认的随机昵称
    private String generateName() {
        SecureRandom random = new SecureRandom();
        StringBuilder lettersPart = new StringBuilder();
        // 生成3个随机字母
        for (int i = 0; i < 3; i++) {
            // 随机选择生成大写或小写字母
            if (random.nextBoolean()) {
                // 大写字母 A-Z (ASCII 65-90)
                lettersPart.append((char) (random.nextInt(26) + 65));
            } else {
                // 小写字母 a-z (ASCII 97-122)
                lettersPart.append((char) (random.nextInt(26) + 97));
            }
        }
        // 生成6位随机数字
        int numbersPart = random.nextInt(900000) + 100000; // 生成范围在 100000-999999 之间的随机数
        // 拼接并返回最终的昵称
        return "user@" + lettersPart + numbersPart;
    }
    // 生成9位数字的用户名
    protected String generateUniqueUsername() {
        String username;
        Random random = new Random();
        username = String.format("%09d", random.nextInt(1_000_000_000));
        return username;
    }


    // 找回密码
    public void resetPass(Long tenantId, String phone, String code, String password1, String password2) {

        // 1. 参数校验
        if (!isValidPhone(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }
        if (!Objects.equals(password1, password2)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 2. 查询用户id
        Long userId = userMapper.selectIdByPhoneAndTenantId(phone, tenantId);
        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 3. 校验验证码
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

        // 4.进入事务
        doResetPass(userId, password1);
    }
    @Transactional
    protected void doResetPass(Long userId, String password) {

        try{
            // 5. 更新密码
            String hashedPassword = PasswordUtils.hash(password);
            userMapper.updateUserPassword(userId, hashedPassword);

            // 6. 更新用户状态
            UserStatus userStatus = confirmSomeUserStatus(userId);
            updateUserStatus(userId, userStatus);
        }catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT, "并发异常");

        } catch (DataAccessException e) {
            logger.error("用户修改密码失败，userId={}", userId, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        // 7. 发送安全通知
        userNotificationService.sendNotification(
                userId,
                "您的密码已重置，如非本人操作请立即联系客服",
                NotificationType.SECURITY
        );
    }

    // 普通用户绑定手机号
    public void bindPhone(Long userId, Long tenantId, String phone, String code) {

        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!this.isValidPhone(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }

        if (!this.existsByPhone(phone, tenantId)) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT);
        }

        // 1. 校验短信验证码（事务外）
        try {
            smsClient.verifySms(new SmsRequest(phone, code));
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("调用 sms-service 校验验证码失败，phone={}", phone, e);
            throw new BusinessException(ErrorCode.DEPENDENCY_ERROR);
        }

        // 2. 进入本地事务
        doBindPhone(userId, phone);
    }
    @Transactional
    protected void doBindPhone(Long userId, String phone) {

        try {
            // 1. 绑定手机号
            boolean rows = userMapper.bindPhone(userId, phone);
            if (!rows) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            // 2. 更新用户状态
            UserStatus userStatus = this.confirmSomeUserStatus(userId);
            this.updateUserStatus(userId, userStatus);

        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT);

        } catch (DataAccessException e) {
            logger.error("用户绑定手机号失败，userId={}, phone={}", userId, phone, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        // 3. 事务提交后发送通知（可异步）
        userNotificationService.sendNotification(
                userId,
                "您已成功绑定手机号:" + phone.substring(0, 3) + "*****" + phone.substring(8),
                NotificationType.SECURITY
        );
    }



    //用户绑定邮箱
    @Transactional
    public void bindEmail(Long userId, Long tenantId, String email, String code){

        if (userId == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        if (!this.isValidEmail(email)) {
            throw new BusinessException(ErrorCode.PARAMS_FORMAT_ERROR);
        }

        if (!this.existsByEmail(email, tenantId)) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT);
        }

        // 验证邮箱验证码
//        try {
//
//        } catch (BusinessException e) {
//
//        } catch (Exception e) {
//
//        }

        doBindEmail(userId, email);
    }
    @Transactional
    protected void doBindEmail(Long userId, String email){
        //绑定邮箱
        try {
            // 1. 绑定邮箱
            boolean rows = userMapper.bindEmail(userId, email);
            if (!rows) {
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            // 2. 更新用户状态
            UserStatus userStatus = this.confirmSomeUserStatus(userId);
            this.updateUserStatus(userId, userStatus);

        } catch (DuplicateKeyException e) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT);

        } catch (DataAccessException e) {
            logger.error("用户绑定邮箱失败，userId={}, email={}", userId, email, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }

        // 3. 事务提交后发送通知（可异步）
        userNotificationService.sendNotification(
                userId,
                "您已成功绑定邮箱:" + email.substring(0, 3) + "*****" + email.substring(8),
                NotificationType.SECURITY
        );
    }


    //获取用户手机号
    public String getUserPhone(Long userId){
        if (userId == null) {
            return "";
        }
        return userMapper.selectUserPhone(userId);
    }

    // 检测该tenant中是否已经存在该手机号
    public boolean existsByPhone(String phone, Long tenantId){
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return userMapper.existsByPhone(phone, tenantId);
    }

    // 检测数据库中是否已经存在该手机号(全局)
    public boolean existsByPhoneGlobal(String phone){
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return userMapper.existsByPhoneGlobal(phone);
    }

    //验证手机号格式是否正确(中国大陆手机号格式)
    public boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        Matcher matcher = this.PHONE_PATTERN.matcher(phone);
        return matcher.matches();
    }

    //检测数据库中是否已经存在该邮箱
    public boolean existsByEmail(String email, Long tenantId){
        if (email == null || email.isEmpty()) {
            return false;
        }
        return userMapper.existsByEmail(email, tenantId);
    }

    // 验证邮箱格式是否正确
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        Matcher matcher = this.EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }


    /**
     * 冻结用户
     *
     * @param userId 用户ID
     * @param hours  冻结时长（小时）
     * @return 是否冻结成功
     */
    @Transactional
    public boolean freezeUser(Long userId, int hours) {

        if (userId == null || hours <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数异常");
        }

        Timestamp frozenUntil = Timestamp.valueOf(
                LocalDateTime.now().plusHours(hours)
        );

        int updated = userMapper.freezeUser(userId, frozenUntil);
        return updated == 1;
    }

    // 检查用户是否已经被锁定
    public boolean isUserFrozen(Long userId) {

        Timestamp frozenUntil = userMapper.selectFrozenUntil(userId);

        return frozenUntil != null && frozenUntil.after(new Timestamp(System.currentTimeMillis()));
    }

//    /**
//     * 删除用户
//     * @param userId 用户id
//     */
//    /**
//     * 删除用户（逻辑删除）
//     * 仅修改状态为 DELETED，并处理关联业务数据
//     */
//    @Transactional
//    public void deleteUser(Long userId) {
//
//        // 1. 参数校验
//        if (userId == null || userId <= 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID非法");
//        }
//
//        // 2. 校验用户是否存在
//        User user = userMapper.selectById(userId);
//        if (user == null) {
//            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
//        }
//
//        // 3. 状态校验：已删除用户不可重复删除
//        if (user.getStatus().equals(UserStatus.DELETED.getCode())) {
//            return; // 幂等处理，直接返回
//        }
//
//        try {
//            // 4. 更新用户状态
//            boolean flag = userMapper.changeUserStatus(userId, UserStatus.DELETED.getCode());
//            if (!flag) {
//                throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除用户失败");
//            }
//
//            // 5. 处理关联业务
////            userProfileMapper.markDeleted(userId);     // 资料隐藏
////            postMapper.disableByUserId(userId);        // 帖子下线
////            commentMapper.disableByUserId(userId);     // 评论下线
////            notificationMapper.clearUserNotice(userId);// 清理通知
//
//            // 6. 可选：写审计日志 / 发送事件
//            // auditLogService.record(...)
//            // domainEventPublisher.publish(new UserDeletedEvent(userId));
//
//        } catch (DataAccessException e) {
//            logger.error("删除用户失败 userId={}", userId, e);
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除用户失败");
//        }
//    }

//
//    //封禁用户
//    @Transactional
//    public void banUser(Long userId, String banReason, Period banPeriod){
//        BannedUser bannedUser = new BannedUser();
//
//        // 设置封禁时间
//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime banEndDateTime = now.plus(banPeriod);
//
//        bannedUser.setUserId(userId);
//        bannedUser.setBanReason(banReason);
//        bannedUser.setBanStartTime(Timestamp.valueOf(now));
//        bannedUser.setBanEndTime(Timestamp.valueOf(banEndDateTime));
//
//        try{
//            // 封禁用户
//            userMapper.changeUserStatus(userId, UserStatus.BANNED.getCode());
//            // 插入封禁用户记录
//            bannedUserMapper.insertBannedUser(bannedUser);
//        }catch (DataAccessException e){
//            logger.error("封禁用户时出错，用户id：{}", userId, e);
//            throw new RuntimeException("封禁用户时出错", e);
//        }
//
//    }


    /**
     * 获取用户的哈希密码
     * @param userId 用户id
     * @return 用户的哈希密码
     */
    public String getPass(Long userId){
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String hashPassword = userMapper.selectPasswordById(userId);
        if(hashPassword == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return hashPassword;
    }


    /**
     * 获取用户id(账号或手机号)，直接根据账号或手机号获取用户id
     * @param tenantId 租户id
     * @param usernameOrPhone 账号或手机号
     * @return 用户id
     */
    public Long getUserId(Long tenantId, String usernameOrPhone){
        // 先尝试根据用户名获取密码
        String hashPassword = userMapper.selectPasswordByUsername(usernameOrPhone);
        if (hashPassword != null) {
            // 如果能通过用户名获取到密码，说明是用户名，返回用户名对应的用户ID
            return userMapper.selectIdByUsername(usernameOrPhone);
        }
        // 如果用户名匹配失败，尝试根据手机号获取密码
        hashPassword = userMapper.selectPasswordByPhoneAndTenantId(usernameOrPhone, tenantId);
        if (hashPassword != null) {
            // 如果能通过手机号获取到密码，说明是手机号，返回手机号对应的用户ID
            return userMapper.selectIdByPhoneAndTenantId(usernameOrPhone, tenantId);
        }
        // 如果都无法匹配，说明用户不存在，返回 null
        return null;
    }

    // 查询用户当前状态
    public UserStatus getUserStatus(Long userId){
        Integer statusNum = userMapper.selectUserStatus(userId);
        //如果查询结果为null，证明该用户不存在
        if(statusNum == null){
            return UserStatus.UNKNOWN;
        }
        return UserStatus.fromCode(statusNum);
    }

    /**
     * 评估用户状态(不涉及特殊状态)
     * @param userId 用户id
     * @return 用户状态
     */
    protected UserStatus confirmSomeUserStatus(Long userId) {
        // 获取该用户的所有信息
        User user = userMapper.selectById(userId);

        // 如果没有绑定手机号，危险状态
        if(user.getPhone() == null){
            return UserStatus.DANGEROUS;
        }

        // 如果没有邮箱，警示状态
        if(user.getEmail() == null){
            return UserStatus.WARNING;
        }

        return UserStatus.SAFE;
    }

    // 更新用户状态
    @Transactional
    public void updateUserStatus(Long userId, UserStatus userStatus){
        try{
            userMapper.changeUserStatus(userId, userStatus.getCode());
        }catch (DataAccessException e){
            logger.error("更改用户状态时出错,用户ID：{}，状态：{}",userId, userStatus, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

}
