package cn.sailstack.qdp.module.trade.dal.dataobject.productbonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 商品奖金结算批次 DO（uk(period, scope)，scope 固定 PRODUCT）
 *
 * @author sail
 */
@TableName("trade_product_bonus_settle_batch")
@KeySequence("trade_product_bonus_settle_batch_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeProductBonusSettleBatchDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 批次号（PB+period）
     */
    private String batchNo;
    /**
     * 账期（yyyy-MM）
     */
    private String period;
    /**
     * 奖金体系（PRODUCT 商品奖金）
     */
    private String scope;
    /**
     * 状态
     * <p>
     * 枚举 {@link cn.sailstack.qdp.module.trade.enums.productbonus.TradeProductBonusSettleStatusEnum}
     */
    private Integer status;
    /**
     * 当月有效 GMV（分）
     */
    private Integer gmvAmount;
    /**
     * 商品奖金池（分，= GMV × product_pool_percent）
     */
    private Integer bonusPool;
    /**
     * 公司留存（分，= 奖金池 × 73%）
     */
    private Integer companyAmount;
    /**
     * 实际分配奖金总金额（分）
     */
    private Integer totalAmount;
    /**
     * 获奖人数
     */
    private Integer userCount;
    /**
     * 结算时规则配置快照（JSON）
     */
    private String configSnapshot;
    /**
     * 备注（例如空档等级说明）
     */
    private String remark;

}
