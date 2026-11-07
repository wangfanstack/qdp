package cn.sailstack.qdp.module.pay.dal.dataobject.channel;

import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.common.enums.CommonStatusEnum;
import cn.sailstack.qdp.framework.common.util.json.JsonUtils;
import cn.sailstack.qdp.module.pay.enums.PayChannelEnum;
import cn.sailstack.qdp.module.pay.framework.pay.core.client.PayClientConfig;
import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import cn.sailstack.qdp.module.pay.dal.dataobject.app.PayAppDO;
import cn.sailstack.qdp.module.pay.framework.pay.core.client.impl.NonePayClientConfig;
import cn.sailstack.qdp.module.pay.framework.pay.core.client.impl.alipay.AlipayAppPayClient;
import cn.sailstack.qdp.module.pay.framework.pay.core.client.impl.alipay.AlipayPayClientConfig;
import cn.sailstack.qdp.module.pay.framework.pay.core.client.impl.weixin.WxPayClientConfig;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.*;

import java.lang.reflect.Field;

/**
 * 支付渠道 DO
 * 一个应用下，会有多种支付渠道，例如说微信支付、支付宝支付等等
 *
 * 即 PayAppDO : PayChannelDO = 1 : n
 *
 * @author 芋道源码
 */
@TableName(value = "pay_channel", autoResultMap = true)
@KeySequence("pay_channel_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayChannelDO extends TenantBaseDO {

    /**
     * 渠道编号，数据库自增
     */
    private Long id;
    /**
     * 渠道编码
     *
     * 枚举 {@link PayChannelEnum}
     */
    private String code;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;
    /**
     * 渠道费率，单位：百分比
     */
    private Double feeRate;
    /**
     * 备注
     */
    private String remark;

    /**
     * 应用编号
     *
     * 关联 {@link PayAppDO#getId()}
     */
    private Long appId;
    /**
     * 支付渠道配置
     */
    @TableField(typeHandler = PayClientConfigTypeHandler.class)
    private PayClientConfig config;

    public static class PayClientConfigTypeHandler extends AbstractJsonTypeHandler<Object> {

        public PayClientConfigTypeHandler(Class<?> type) {
            super(type);
        }

        public PayClientConfigTypeHandler(Class<?> type, Field field) {
            super(type, field);
        }

        @Override
        public Object parse(String json) {
            // 兼容上游 yudao 初始化数据：@class 存储的是 cn.iocoder.yudao 包路径，本项目改名后包结构有差异：
            // 上游 framework.pay starter 已合并进 qdp-module-pay，client 类位于 module.pay.framework.pay 下
            json = org.apache.commons.lang3.StringUtils.replace(json,
                    "cn.iocoder.yudao.framework.pay.core.client", "cn.sailstack.qdp.module.pay.framework.pay.core.client");
            json = org.apache.commons.lang3.StringUtils.replace(json, "cn.iocoder.yudao", "cn.sailstack.qdp");
            PayClientConfig config = JsonUtils.parseObjectQuietly(json, new TypeReference<>() {});
            if (config != null) {
                return config;
            }

            // 兼容老版本的包路径：取全限定类名的 simple name（上游 hutool subAfter(str, ".", true) 的等价语义）
            String className = JsonUtils.parseObject(json, "@class", String.class);
            className = org.apache.commons.lang3.StringUtils.substringAfterLast(className, ".");
            switch (className) {
                case "AlipayPayClientConfig":
                    return JsonUtils.parseObject2(json, AlipayPayClientConfig.class);
                case "WxPayClientConfig":
                    return JsonUtils.parseObject2(json, WxPayClientConfig.class);
                case "NonePayClientConfig":
                    return JsonUtils.parseObject2(json, NonePayClientConfig.class);
                default:
                    throw new IllegalArgumentException("未知的 PayClientConfig 类型：" + json);
            }
        }

        @Override
        public String toJson(Object obj) {
            return JsonUtils.toJsonString(obj);
        }

    }

}
