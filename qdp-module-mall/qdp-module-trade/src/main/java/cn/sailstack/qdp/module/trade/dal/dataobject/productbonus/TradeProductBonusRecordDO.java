package cn.sailstack.qdp.module.trade.dal.dataobject.productbonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 商品奖金明细 DO（折扣差额奖 + 商品董事加权分红）
 *
 * uk(dedup_key) 防重复计奖：差额奖=订单项维度；董事分红=账期+会员+等级维度。
 * 与礼包奖金（member_bonus_record）独立核算。
 *
 * @author sail
 */
@TableName("trade_product_bonus_record")
@KeySequence("trade_product_bonus_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeProductBonusRecordDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 防重复键（差额奖=item:DIFF；董事分红=period:会员:等级）
     */
    private String dedupKey;
    /**
     * 获奖会员编号（差额奖=买家直接上级）
     */
    private Long userId;
    /**
     * 奖励类型
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusTypeEnum}
     */
    private Integer bonusType;
    /**
     * 购买会员编号（差额奖）
     */
    private Long buyerUserId;
    /**
     * 来源订单编号（差额奖）
     */
    private Long orderId;
    /**
     * 来源订单项编号（差额奖）
     */
    private Long orderItemId;
    /**
     * 购买会员折扣（百分比，60 表示 6 折）
     */
    private Integer buyerDiscount;
    /**
     * 直接上级折扣（百分比）
     */
    private Integer superiorDiscount;
    /**
     * 计奖基数（分，差额奖=订单项实付；董事=等级预算）
     */
    private Integer baseAmount;
    /**
     * 奖励比例（百分比，差额奖=折扣差）
     */
    private Integer rate;
    /**
     * 奖金金额（分，冲正为负）
     */
    private Integer amount;
    /**
     * 董事等级（商品董事加权分红）
     */
    private Integer directorLevel;
    /**
     * 所属账期（yyyy-MM，差额奖为订单完成所在月，董事分红必填）
     */
    private String period;
    /**
     * 计算时规则版本
     */
    private Integer ruleVersion;
    /**
     * 状态
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusStatusEnum}
     */
    private Integer status;
    /**
     * 结算批次号
     */
    private String settleBatchNo;
    /**
     * 冲正关联的原奖金编号
     */
    private Long reversalOfId;
    /**
     * 冲正原因
     */
    private String reversalReason;

}
