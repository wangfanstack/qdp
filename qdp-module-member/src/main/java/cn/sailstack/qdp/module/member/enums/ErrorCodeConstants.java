package cn.sailstack.qdp.module.member.enums;

import cn.sailstack.qdp.framework.common.exception.ErrorCode;

/**
 * Member 错误码枚举类
 * <p>
 * member 系统，使用 1-004-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== 用户相关  1-004-001-000 ============
    ErrorCode USER_NOT_EXISTS = new ErrorCode(1_004_001_000, "用户不存在");
    ErrorCode USER_MOBILE_NOT_EXISTS = new ErrorCode(1_004_001_001, "手机号未注册用户");
    ErrorCode USER_MOBILE_USED = new ErrorCode(1_004_001_002, "修改手机失败，该手机号({})已经被使用");
    ErrorCode USER_POINT_NOT_ENOUGH = new ErrorCode(1_004_001_003, "用户积分余额不足");
    ErrorCode USER_EMAIL_USED = new ErrorCode(1_004_001_004, "修改邮箱失败，该邮箱({})已经被使用");

    // ========== AUTH 模块 1-004-003-000 ==========
    ErrorCode AUTH_LOGIN_BAD_CREDENTIALS = new ErrorCode(1_004_003_000, "登录失败，账号密码不正确");
    ErrorCode AUTH_LOGIN_USER_DISABLED = new ErrorCode(1_004_003_001, "登录失败，账号被禁用");
    ErrorCode AUTH_SOCIAL_USER_NOT_FOUND = new ErrorCode(1_004_003_005, "登录失败，解析不到三方登录信息");
    ErrorCode AUTH_MOBILE_USED = new ErrorCode(1_004_003_007, "手机号已经被使用");

    // ========== 用户收件地址 1-004-004-000 ==========
    ErrorCode ADDRESS_NOT_EXISTS = new ErrorCode(1_004_004_000, "用户收件地址不存在");

    //========== 用户标签 1-004-006-000 ==========
    ErrorCode TAG_NOT_EXISTS = new ErrorCode(1_004_006_000, "用户标签不存在");
    ErrorCode TAG_NAME_EXISTS = new ErrorCode(1_004_006_001, "用户标签已经存在");
    ErrorCode TAG_HAS_USER = new ErrorCode(1_004_006_002, "用户标签下存在用户，无法删除");

    //========== 积分配置 1-004-007-000 ==========

    //========== 积分记录 1-004-008-000 ==========
    ErrorCode POINT_RECORD_BIZ_NOT_SUPPORT = new ErrorCode(1_004_008_000, "用户积分记录业务类型不支持");

    //========== 签到配置 1-004-009-000 ==========
    ErrorCode SIGN_IN_CONFIG_NOT_EXISTS = new ErrorCode(1_004_009_000, "签到天数规则不存在");
    ErrorCode SIGN_IN_CONFIG_EXISTS = new ErrorCode(1_004_009_001, "签到天数规则已存在");

    //========== 签到配置 1-004-010-000 ==========
    ErrorCode SIGN_IN_RECORD_TODAY_EXISTS = new ErrorCode(1_004_010_000, "今日已签到，请勿重复签到");

    //========== 用户等级 1-004-011-000 ==========
    ErrorCode LEVEL_NOT_EXISTS = new ErrorCode(1_004_011_000, "用户等级不存在");
    ErrorCode LEVEL_NAME_EXISTS = new ErrorCode(1_004_011_001, "用户等级名称[{}]已被使用");
    ErrorCode LEVEL_VALUE_EXISTS = new ErrorCode(1_004_011_002, "用户等级值[{}]已被[{}]使用");
    ErrorCode LEVEL_EXPERIENCE_MIN = new ErrorCode(1_004_011_003, "升级经验必须大于上一个等级[{}]设置的升级经验[{}]");
    ErrorCode LEVEL_EXPERIENCE_MAX = new ErrorCode(1_004_011_004, "升级经验必须小于下一个等级[{}]设置的升级经验[{}]");
    ErrorCode LEVEL_HAS_USER = new ErrorCode(1_004_011_005, "用户等级下存在用户，无法删除");

    ErrorCode EXPERIENCE_BIZ_NOT_SUPPORT = new ErrorCode(1_004_011_201, "用户经验业务类型不支持");

    //========== 用户分组 1-004-012-000 ==========
    ErrorCode GROUP_NOT_EXISTS = new ErrorCode(1_004_012_000, "用户分组不存在");
    ErrorCode GROUP_HAS_USER = new ErrorCode(1_004_012_001, "用户分组下存在用户，无法删除");

    //========== 礼包配置 1-004-013-000 ==========
    ErrorCode GIFT_CONFIG_NOT_EXISTS = new ErrorCode(1_004_013_000, "礼包配置不存在");
    ErrorCode GIFT_CONFIG_NAME_EXISTS = new ErrorCode(1_004_013_001, "礼包名称[{}]已被使用");
    ErrorCode GIFT_CONFIG_SPU_EXISTS = new ErrorCode(1_004_013_002, "商品[{}]已被其他礼包绑定");
    ErrorCode GIFT_CONFIG_DISABLED = new ErrorCode(1_004_013_003, "礼包[{}]未开启");
    ErrorCode GIFT_CONFIG_HAS_BENEFIT = new ErrorCode(1_004_013_004, "该礼包已产生权益记录，仅可停用不可删除");

    //========== 礼包权益 1-004-014-000 ==========
    ErrorCode GIFT_BENEFIT_NOT_EXISTS = new ErrorCode(1_004_014_000, "礼包权益记录不存在");
    ErrorCode GIFT_BENEFIT_ALREADY_REVERSED = new ErrorCode(1_004_014_001, "礼包权益已冲减");
    ErrorCode GIFT_BUY_LEVEL_NOT_HIGHER = new ErrorCode(1_004_014_002, "当前会员等级不低于该礼包档位，仅可购买更高等级礼包");
    ErrorCode GIFT_BUY_NEED_REFERRER = new ErrorCode(1_004_014_003, "开通会员礼包前必须绑定推荐人");
    ErrorCode GIFT_BUY_AMOUNT_NOT_MATCH = new ErrorCode(1_004_014_004, "订单应付金额与系统计算不一致，请重新下单");

    //========== 推荐关系 1-004-015-000 ==========
    ErrorCode RELATION_NOT_EXISTS = new ErrorCode(1_004_015_000, "推荐关系不存在");
    ErrorCode RELATION_EXISTS = new ErrorCode(1_004_015_001, "已绑定推荐人，不可重复绑定");
    ErrorCode RELATION_REFERRER_NOT_EXISTS = new ErrorCode(1_004_015_002, "推荐人不存在");
    ErrorCode RELATION_BIND_SELF = new ErrorCode(1_004_015_003, "不能绑定自己为推荐人");
    ErrorCode RELATION_BIND_LOOP = new ErrorCode(1_004_015_004, "推荐关系不能形成环路");

    //========== 礼包奖金 1-004-016-000 ==========
    ErrorCode BONUS_CONFIG_NOT_EXISTS = new ErrorCode(1_004_016_000, "奖金规则配置不存在");
    ErrorCode BONUS_SETTLE_BATCH_NOT_EXISTS = new ErrorCode(1_004_016_001, "结算批次不存在");
    ErrorCode BONUS_SETTLE_BATCH_EXISTS = new ErrorCode(1_004_016_002, "账期[{}]已存在结算批次，不可重复结算");
    ErrorCode BONUS_SETTLE_BATCH_STATUS_ERROR = new ErrorCode(1_004_016_003, "结算批次[{}]状态不允许该操作");
    ErrorCode BONUS_SETTLE_INCOME_DISABLED = new ErrorCode(1_004_016_004, "礼包董事分红池收入口径未配置，禁止结算董事分红");
    ErrorCode BONUS_DIRECTOR_LEVEL_INVALID = new ErrorCode(1_004_016_005, "董事等级[{}]不合法");

}
